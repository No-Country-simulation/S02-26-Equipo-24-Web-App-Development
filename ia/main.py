import time
from client import JustinaAIClient
from analysis_pipeline import run_pipeline
from typing import List

def procesar_cirugia(client: JustinaAIClient, surgery_id: str) -> bool:
    print(f"\n{'-'*50}")
    print(f"🏥 PROCESANDO CIRUGÍA: {surgery_id}")
    print(f"{'-'*50}")
    
    # 1. Obtener trayectoria
    data = client.get_trajectory(surgery_id)
    if not data:
        return False

    # 2. Analizar
    print("🧠 Analizando trayectoria con pipeline completo...")
    score, feedback = run_pipeline(data)

    # 3. Enviar
    return client.send_analysis(surgery_id, score, feedback)

def procesar_batch(client: JustinaAIClient, surgery_ids: List[str]):
    print(f"\n🚀 Procesando {len(surgery_ids)} cirugías en lote...")
    exitosas = 0
    
    for i, sid in enumerate(surgery_ids, 1):
        print(f"\n[{i}/{len(surgery_ids)}]")
        if procesar_cirugia(client, sid):
            exitosas += 1
        time.sleep(1)
        
    print(f"\n{'='*50}")
    print(f"📊 RESUMEN: {exitosas}/{len(surgery_ids)} cirugías procesadas correctamente.")
    print(f"{'='*50}")

def main():
    print("""
╔════════════════════════════════════════╗
║   JUSTINA - SISTEMA DE IA AVANZADO    ║
╚════════════════════════════════════════╝
""")
    client = JustinaAIClient()
    
    while True:
        print("\nOpciones:")
        print("1. Procesar una cirugía (ID)")
        print("2. Procesar lote de ejemplo")
        print("3. Salir")
        
        opcion = input("\nSeleccione una opción: ")
        
        if opcion == "1":
            if client.ensure_authenticated():
                sid = input("Ingrese el UUID de la cirugía: ")
                if sid:
                    procesar_cirugia(client, sid)
                else:
                    print("⚠️ ID vacío")
        elif opcion == "2":
            if client.ensure_authenticated():
                batch_ids = [
                    "123e4567-e89b-12d3-a456-426614174000",
                    "223e4567-e89b-12d3-a456-426614174001"
                ]
                procesar_batch(client, batch_ids)
        elif opcion == "3":
            print("👋 Saliendo...")
            break
        else:
            print("❌ Opción inválida")

if __name__ == "__main__":
    main()