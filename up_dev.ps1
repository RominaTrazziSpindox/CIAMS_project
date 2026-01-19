# ---------------------------------------------
# Loading DEV configuration with Podman
# ---------------------------------------------

Write-Host 'Loading DEV environment variables from .env.dev'

# Copy.env.dev → .env (file for docker-compose)
Copy-Item .env.dev .env -Force

Write-Host 'Using .env.dev'
Get-Content .env | Where-Object { $_ -match 'SPRING_PROFILES_ACTIVE' }

# Start Podman compose using docker-compose.yml
podman compose -f docker-compose.yml up --build