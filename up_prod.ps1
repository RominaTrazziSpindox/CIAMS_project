# ---------------------------------------------
# Loading PROD configuration with Podman
# ---------------------------------------------

Write-Host "Loading PROD environment variables from .env.prod"

# Copy .env.prod → .env (file for docker-compose)
Copy-Item .env.prod .env -Force

Write-Host 'Using .env.prod'

Get-Content .env | Where-Object { $_ -match 'SPRING_PROFILES_ACTIVE' }

# Start Podman compose using docker-compose.yml
podman compose -f docker-compose.yml up --build