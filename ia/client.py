import requests
import time
from config import BASE_URL, IA_USERNAME, IA_PASSWORD, REQUEST_TIMEOUT, RETRY_ATTEMPTS


class JustinaAIClient:
    def __init__(self):
        self.base_url = BASE_URL
        self.token = None
        self.token_expiration = None

    def login(self):
        url = f"{self.base_url}/api/v1/auth/login"
        payload = {
            "username": IA_USERNAME,
            "password": IA_PASSWORD
        }

        response = requests.post(url, json=payload, timeout=REQUEST_TIMEOUT)

        if response.status_code != 200:
            raise Exception("Error autenticando IA")

        data = response.json()
        self.token = data["token"]
        self.token_expiration = time.time() + (60 * 60 * 24)

        print("✅ Login exitoso")

    def ensure_authenticated(self):
        if not self.token or time.time() > self.token_expiration:
            self.login()

    def get_trajectory(self, surgery_id):
        self.ensure_authenticated()

        url = f"{self.base_url}/api/v1/surgeries/{surgery_id}/trajectory"
        headers = {"Authorization": f"Bearer {self.token}"}

        response = requests.get(url, headers=headers, timeout=REQUEST_TIMEOUT)

        if response.status_code == 200:
            return response.json()
        else:
            print("Error obteniendo trayectoria:", response.status_code)
            return None

    def send_analysis(self, surgery_id, score, feedback):
        self.ensure_authenticated()

        url = f"{self.base_url}/api/v1/surgeries/{surgery_id}/analysis"
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }

        payload = {
            "score": float(score),
            "feedback": feedback
        }

        response = requests.post(url, json=payload, headers=headers)

        if response.status_code == 204:
            print("✅ Análisis enviado correctamente")
            return True
        else:
            print("Error enviando análisis:", response.text)
            return False