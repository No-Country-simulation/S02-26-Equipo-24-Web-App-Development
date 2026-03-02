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

        print(f"📝 Iniciando login como IA en {self.base_url}...")
        try:
            response = requests.post(url, json=payload, timeout=REQUEST_TIMEOUT)
            if response.status_code != 200:
                print(f"❌ Error en login: {response.status_code}")
                return False

            data = response.json()
            self.token = response.cookies.get("jwt-token")
            
            # Fallback en caso de que aún venga en el JSON
            if not self.token:
                self.token = data.get("token")
                
            if not self.token:
                print("❌ No se encontró el token de autenticación en la respuesta")
                return False
                
            self.token_expiration = time.time() + (60 * 60 * 24)
            print("✅ Login exitoso")
            return True
        except Exception as e:
            print(f"❌ Error de conexión: {e}")
            return False

    def ensure_authenticated(self):
        if not self.token or time.time() > self.token_expiration:
            return self.login()
        return True

    def get_trajectory(self, surgery_id):
        if not self.ensure_authenticated():
            return None

        url = f"{self.base_url}/api/v1/surgeries/{surgery_id}/trajectory"
        headers = {"Authorization": f"Bearer {self.token}"}

        print(f"\n🔍 Obteniendo trayectoria de cirugía {surgery_id}...")
        try:
            response = requests.get(url, headers=headers, timeout=REQUEST_TIMEOUT)
            if response.status_code == 200:
                data = response.json()
                print(f"✅ Trayectoria obtenida ({len(data['movements'])} movimientos)")
                return data
            elif response.status_code == 404:
                print(f"❌ Cirugía {surgery_id} no encontrada")
            else:
                print(f"❌ Error obteniendo trayectoria: {response.status_code}")
            return None
        except Exception as e:
            print(f"❌ Error de red: {e}")
            return None

    def send_analysis(self, surgery_id, score, feedback):
        if not self.ensure_authenticated():
            return False

        url = f"{self.base_url}/api/v1/surgeries/{surgery_id}/analysis"
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }

        payload = {
            "score": float(score),
            "feedback": feedback
        }

        print(f"📤 Enviando análisis para cirugía {surgery_id}...")
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=REQUEST_TIMEOUT)
            if response.status_code == 204:
                print("✅ Análisis enviado correctamente")
                return True
            else:
                print(f"❌ Error enviando análisis: {response.status_code} - {response.text}")
                return False
        except Exception as e:
            print(f"❌ Error de red al enviar análisis: {e}")
            return False
