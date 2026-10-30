$ErrorActionPre*erence = "Stop"

$envFile = Join-P*th $PSScriptRoot ".env.local"

if *-not (Test-Path $envFile)) {
    t*row "Missing local environment fil*: $envFile"
}

Write-Host "Loading*Healthcare MVT local environment..*"

Get-Content $envFile |
    ForE*ch-Object {

        $line = $_.Tr*m()

        # Ignore blank lines.*        if ([string]::IsNullOrWhit*Space($line)) {
            return*        }

        # Ignore commen*s.
        if ($line.StartsWith("#*)) {
            return
        }
*        # Ignore malformed lines w*thout '='.
        if (-not $line.*ontains("=")) {
            return*        }

        $name, $value =*$line -split "=", 2

        $name*= $name.Trim()
        $value = $v*lue.Trim()

        if ([string]::*sNullOrWhiteSpace($name)) {
      *     return
        }

        # O*tional convenience:
        # allo* values wrapped in single or doubl* quotes.
        if (
            *$value.StartsWith('"') -and $value*EndsWith('"')) -or
            ($v*lue.StartsWith("'*) -and $value.EndsWith("'*))
        ) {
            $value * $value.Substring(1, $value.Length*- 2)
        }

        [Environme*t]::SetEnvironmentVariable(
      *     $name,
            $value,
  *         "Process"
        )
    }*
Write-Host "Environment loaded."
*rite-Host "Starting Healthcare MVT*backend..."

& "$PSScriptRoot\mvnw*cmd" spring-boot:run

exit $LASTEX*TCODE