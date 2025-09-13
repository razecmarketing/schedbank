param(
    [switch]$SkipTests
)

$mvncmd = "mvn clean package"
if ($SkipTests) { $mvncmd += " -DskipTests=true" }
Write-Host "Running: $mvncmd"

$proc = Start-Process -FilePath mvn -ArgumentList ($mvncmd -split ' ') -NoNewWindow -Wait -PassThru
if ($proc.ExitCode -ne 0) {
    Write-Error "Maven build failed with exit code $($proc.ExitCode)"
    exit $proc.ExitCode
}

$jar = Get-ChildItem -Path target -Filter "*.jar" | Select-Object -First 1
if (-not $jar) {
    Write-Error "Jar not found in target folder"
    exit 1
}

Write-Host "Starting jar: $($jar.FullName)"
Start-Process -FilePath java -ArgumentList ('-jar', $jar.FullName, '--spring.main.banner-mode=off') -NoNewWindow -PassThru
