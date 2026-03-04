"""
websocket_client.py
Cliente WebSocket para recibir notificaciones en tiempo real del backend
"""

import websocket
import json
import threading
import time
import os  
import sys
import functools
from flask import Flask  
from client import JustinaAIClient
from analysis_pipeline import run_pipeline
from config import BASE_URL

# Forzar a Python a no bufferear los prints (revelará los logs ocultos en Render)
print = functools.partial(print, flush=True)

# ========================================================
# 🚀 BYPASS PARA EL PLAN GRATUITO DE RENDER
# ========================================================
app = Flask(__name__)

@app.route('/')
def health_check():
    return "Justina AI WebSocket Client is Alive!", 200

def run_health_server():
    port = int(os.environ.get("PORT", 8000))

    app.run(host='0.0.0.0', port=port)

# Iniciamos el servidor en segundo plano antes de todo lo demás
threading.Thread(target=run_health_server, daemon=True).start()
# ========================================================


class AIWebSocketClient:
    def __init__(self):
        self.client = JustinaAIClient()
        self.ws = None
        self.running = False
        self.reconnect_delay = 5  # segundos
        
    def get_websocket_url(self):
        """Construir URL WebSocket desde BASE_URL"""
        # Convertir http:// a ws:// y https:// a wss://
        ws_url = BASE_URL.replace("http://", "ws://").replace("https://", "wss://")
        return f"{ws_url}/ws/ai?token={self.client.token}"
    
    def on_message(self, ws, message):
        """Callback cuando llega un mensaje del backend"""
        try:
            print(f"\n{'='*60}")
            print(f"📡 MENSAJE RAW DEL SERVIDOR WEBSOCKET:")
            print(f"{message}")
            print(f"{'='*60}\n")
            
            data = json.loads(message)
            
            # Verificar si es notificación de nueva cirugía
            if data.get("event") == "NEW_SURGERY":
                surgery_id = data.get("surgeryId")
                print(f"\n🔔 Nueva cirugía detectada: {surgery_id}")
                
                # Procesar en un thread separado para no bloquear el WebSocket
                thread = threading.Thread(
                    target=self.procesar_cirugia_async,
                    args=(surgery_id,)
                )
                thread.daemon = True
                thread.start()
                
            elif data.get("status") == "connected":
                print(f"✅ {data.get('message', 'Conectado al backend')}")
                
        except json.JSONDecodeError as e:
            print(f"❌ Error parseando mensaje: {e}")
        except Exception as e:
            print(f"❌ Error procesando mensaje: {e}")
    
    def procesar_cirugia_async(self, surgery_id):
        """Procesar cirugía en thread separado"""
        try:
            print(f"\n{'='*60}")
            print(f"🏥 INICIANDO ANÁLISIS: {surgery_id}")
            print(f"{'='*60}")
            
            # 1. Obtener trayectoria
            print("📊 Paso 1: Obteniendo trayectoria...")
            trajectory_data = self.client.get_trajectory(surgery_id)
            
            if not trajectory_data:
                print(f"❌ No se pudo obtener trayectoria para {surgery_id}")
                return
            
            # 2. Analizar con el pipeline
            print("🧠 Paso 2: Analizando con pipeline de 5 pasos...")
            score, feedback = run_pipeline(trajectory_data)
            
            print(f"✅ Análisis completado - Score: {score:.1f}/100")
            
            # 3. Enviar resultado
            print("📤 Paso 3: Enviando análisis al backend...")
            success = self.client.send_analysis(surgery_id, score, feedback)
            
            if success:
                print(f"🎉 ¡Cirugía {surgery_id} procesada exitosamente!")
            else:
                print(f"⚠️ Error al enviar análisis para {surgery_id}")
            
            print(f"{'='*60}\n")
            
        except Exception as e:
            print(f"❌ Error procesando cirugía {surgery_id}: {e}")
            import traceback
            traceback.print_exc()
    
    def on_error(self, ws, error):
        """Callback cuando hay un error"""
        print(f"❌ Error WebSocket: {error}")
    
    def on_close(self, ws, close_status_code, close_msg):
        """Callback cuando se cierra la conexión"""
        print(f"\n🔌 WebSocket cerrado (código: {close_status_code})")
        if close_msg:
            print(f"   Mensaje: {close_msg}")
        
        # Intentar reconectar si aún estamos corriendo
        if self.running:
            print(f"🔄 Reconectando en {self.reconnect_delay}s...")
            time.sleep(self.reconnect_delay)
            self.connect()
    
    def on_open(self, ws):
        """Callback cuando se abre la conexión"""
        print("🔌 WebSocket conectado exitosamente")
        print("👂 Esperando notificaciones del backend...")
    
    def connect(self):
        """Conectar al WebSocket del backend"""
        # Asegurar autenticación
        if not self.client.ensure_authenticated():
            print("❌ No se pudo autenticar. Reintentando en 10s...")
            time.sleep(10)
            return self.connect()
        
        # Construir URL
        ws_url = self.get_websocket_url()
        print(f"\n🌍 Conectando a: {ws_url}")
        
        # Crear WebSocket con callbacks
        self.ws = websocket.WebSocketApp(
            ws_url,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close,
            on_open=self.on_open
        )
        
        # Correr en loop (bloqueante)
        self.ws.run_forever()
    
    def start(self):
        """Iniciar el cliente WebSocket"""
        print("""
╔════════════════════════════════════════════════════════╗
║   JUSTINA - CLIENTE IA CON WEBSOCKET                  ║
║   Escuchando notificaciones en tiempo real...         ║
╚════════════════════════════════════════════════════════╝
        """)
        
        self.running = True
        
        try:
            self.connect()
        except KeyboardInterrupt:
            print("\n\n🛑 Deteniendo cliente...")
            self.running = False
            if self.ws:
                self.ws.close()
        except Exception as e:
            print(f"❌ Error fatal: {e}")
            import traceback
            traceback.print_exc()
    
    def stop(self):
        """Detener el cliente"""
        self.running = False
        if self.ws:
            self.ws.close()


def main():
    """Punto de entrada principal"""
    client = AIWebSocketClient()
    
    print("🚀 Iniciando cliente de IA...")
    print(f"📡 Backend: {BASE_URL}")
    print(f"🔐 Usuario: {client.client.base_url}")
    print("\n💡 Presiona Ctrl+C para detener\n")
    
    try:
        client.start()
    except KeyboardInterrupt:
        print("\n👋 Saliendo...")
        client.stop()


if __name__ == "__main__":
    main()