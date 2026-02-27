import os

# URL del backend (puede venir de variable de entorno)
BASE_URL = os.getenv("BACKEND_URL", "http://localhost:8080")

# Credenciales de la IA
IA_USERNAME = os.getenv("IA_USERNAME", "ia_justina")
IA_PASSWORD = os.getenv("IA_PASSWORD", "ia_secret_2024")

# Configuración de requests
REQUEST_TIMEOUT = int(os.getenv("REQUEST_TIMEOUT", "10"))
RETRY_ATTEMPTS = int(os.getenv("RETRY_ATTEMPTS", "3"))
