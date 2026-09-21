#!/usr/bin/env bash
# Fails when the Maven canary analysed fewer source roots than the fixture has.
#
# The reason this exists: an analysis that misses `src/main/kotlin` uploads successfully and the
# job goes green. Four consumer repositories passed that way while most of their Kotlin was never
# indexed, so job outcome cannot be the assertion.
#
# It reads the log of the run's own Maven Sonar job rather than re-running the scanner, so what is
# asserted is the invocation that actually happened.
#
# Inputs (environment):
#   GH_TOKEN         needs `actions: read`
#   GITHUB_REPOSITORY, GITHUB_RUN_ID
set -euo pipefail

expected_main_kotlin=2
expected_test_kotlin=2

log=$(mktemp)
trap 'rm -f "$log"' EXIT

mapfile -t job_ids < <(
  gh api --paginate "repos/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}/jobs" \
    --jq '.jobs[] | select(.name | contains("sonar/maven")) | .id'
)

if [[ ${#job_ids[@]} -eq 0 ]]; then
  echo "::error title=No Maven Sonar job::Nothing in run ${GITHUB_RUN_ID} is named sonar/maven, so nothing was asserted." >&2
  exit 1
fi

for id in "${job_ids[@]}"; do
  gh api "repos/${GITHUB_REPOSITORY}/actions/jobs/${id}/logs" >> "$log"
done

count_roots() {
  grep -cE "^.*${1}:.*${2}" "$log" || true
}

main_kotlin=$(count_roots "Source paths" "src/main/kotlin")
test_kotlin=$(count_roots "Test paths" "src/test/kotlin")

echo "Modules indexing src/main/kotlin: ${main_kotlin} (expected ${expected_main_kotlin})"
echo "Modules indexing src/test/kotlin: ${test_kotlin} (expected ${expected_test_kotlin})"

failed=0
if [[ "$main_kotlin" -lt "$expected_main_kotlin" ]]; then
  echo "::error title=Kotlin sources missing from analysis::${main_kotlin} of ${expected_main_kotlin} modules indexed src/main/kotlin. The scanner runs as a separate mvn process; the roots have to be registered in a phase it reaches." >&2
  failed=1
fi
if [[ "$test_kotlin" -lt "$expected_test_kotlin" ]]; then
  echo "::error title=Kotlin tests missing from analysis::${test_kotlin} of ${expected_test_kotlin} modules indexed src/test/kotlin. add-test-source bound past generate-sources never runs." >&2
  failed=1
fi
exit "$failed"
