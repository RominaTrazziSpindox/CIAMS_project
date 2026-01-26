# =============================================
# Podman Compose startup script (DEV / PROD)
# =============================================
# This script:
# 1. Selects the environment (dev or prod)
# 2. Copies the .env.<env> file to .env
#    (Podman Compose reads ONLY .env)
# 3. Displays the active Spring profile
# 4. Starts podman compose using docker-compose.yml
# =============================================

param (
    # Parameter that specifies the environment to use.
    # Allowed values: "dev" or "prod".
    # Default is "dev" if not provided.
    [ValidateSet("dev", "prod")]
    [string]$Env = "dev"
)

# Informational message to avoid ambiguity
Write-Host "Loading $($Env.ToUpper()) environment variables"

# Build the environment file name
# dev  -> .env.dev
# prod -> .env.prod
$envFile = ".env.$Env"

# Safety check:
# if the .env.<env> file does not exist, stop execution
if (-Not (Test-Path $envFile)) {
    Write-Error "Environment file '$envFile' not found. Aborting."
    exit 1
}

# ---------------------------------------------
# KEY STEP
# Copy the environment file to .env
# Podman Compose automatically reads ONLY .env
# ---------------------------------------------
Copy-Item $envFile .env -Force

# Visual confirmation of the file being used
Write-Host "Using environment file: $envFile"

# Print only the active Spring profile
# (quick debug to avoid environment mistakes)
Get-Content .env | Where-Object { $_ -match 'SPRING_PROFILES_ACTIVE' }

# ---------------------------------------------
# Start Podman Compose
# Uses docker-compose.yml
# Reads variables from the .env file just copied
# ---------------------------------------------
podman compose -f docker-compose.yml up --build