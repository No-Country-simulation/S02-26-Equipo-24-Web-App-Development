import sys
import os

# Asegurar que podemos importar desde el directorio actual
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from analysis_pipeline import run_pipeline

def test_pipeline_locally():
    print("🧪 Probando Pipeline de IA con datos sintéticos...")
    
    # Simular datos de trayectoria
    trajectory_data = {
        "movements": [
            {"coordinates": [0, 0, 0], "event": "START", "timestamp": 1709140000000},
            {"coordinates": [1, 2, 1], "event": "NONE", "timestamp": 1709140001000},
            {"coordinates": [2, 4, 2], "event": "TUMOR_TOUCH", "timestamp": 1709140002000},
            {"coordinates": [3, 2, 1], "event": "HEMORRHAGE", "timestamp": 1709140003000},
            {"coordinates": [5, 5, 5], "event": "FINISH", "timestamp": 1709140005000}
        ]
    }
    
    try:
        score, feedback = run_pipeline(trajectory_data)
        
        print(f"\n✅ Pipeline ejecutado correctamente")
        print(f"📊 Score Resultante: {score}")
        print("-" * 30)
        print("📝 FEEDBACK GENERADO:")
        print(feedback)
        print("-" * 30)
        
        # Validaciones básicas
        assert 0 <= score <= 100, f"Score fuera de rango: {score}"
        assert "ALERTAS CRÍTICAS" in feedback, "Falta sección de alertas"
        assert "MÉTRICAS DE DESTREZA" in feedback, "Falta sección de métricas"
        
        print("\n✨ ¡Prueba local exitosa!")
        
    except Exception as e:
        print(f"\n❌ Error durante la prueba: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_pipeline_locally()
