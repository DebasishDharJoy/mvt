$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot ".env.local"

if (-not (Test-Path $envFile)) {
    throw "Missing local environment file: $envFile"
}

Write-Host "Loading Healthcare MVT local environment..."

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()

    # Ignore blank lines.
    if ([string]::IsNullOrWhiteSpace($line)) {
        return
    }

    # Ignore comments.
    if ($line.StartsWith("#")) {
        return
    }

    # Ignore malformed lines without '='.
    if (-not $line.Contains("=")) {
        return
    }

    $name, $value = $line -split "=", 2

    $name = $name.Trim()
    $value = $value.Trim()

    if ([string]::IsNullOrWhiteSpace($name)) {
        return
    }

    # Optional convenience:
    # allow values wrapped in single or double quotes.
    if (
        ($value.StartsWith('"') -and $value.EndsWith('"')) -or
        ($value.StartsWith("'") -and $value.EndsWith("'"))
    ) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    [Environment]::SetEnvironmentVariable($name, $value, "Process")
}

Write-Host "Environment loaded."
Write-Host "Starting Healthcare MVT backend..."

& "$PSScriptRoot\mvnw.cmd" spring-boot:run

exit $LASTEXITCODE