import sys
import os

# Asegurar que podemos importar desde el directorio actual
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from analysis_pipeline import run_pipeline

def test_pipeline_locally():
    print("🧪 Probando Nuevo Scoring de IA...")
    
    # 1. ESCENARIO: CIRUGÍA PERFECTA (25 fragmentos eliminados, 0 errores)
    perfect_data = {
        "movements": [
            {"coordinates": [0, 0, 0], "event": "START", "timestamp": 1000},
            *[{"coordinates": [1, 1, 1], "event": "TUMOR_REMOVAL", "timestamp": 2000 + i} for i in range(25)],
            {"coordinates": [0, 0, 0], "event": "FINISH", "timestamp": 5000}
        ]
    }
    
    # 2. ESCENARIO: CIRUGÍA CON ERRORES (10 fragmentos, 5 toques riñón, 1 hemorragia)
    error_data = {
        "movements": [
            {"coordinates": [0, 0, 0], "event": "START", "timestamp": 1000},
            *[{"coordinates": [1, 1, 1], "event": "TUMOR_REMOVAL", "timestamp": 2000 + i} for i in range(10)],
            *[{"coordinates": [1, 1, 1], "event": "KIDNEY_TOUCH", "timestamp": 3000 + i} for i in range(5)],
            {"coordinates": [1, 1, 1], "event": "HEMORRHAGE", "timestamp": 4000},
            {"coordinates": [0, 0, 0], "event": "FINISH", "timestamp": 5000}
        ]
    }
    
    try:
        print("\n--- TEST 1: CIRUGÍA PERFECTA ---")
        score1, feedback1 = run_pipeline(perfect_data)
        print(f"Score: {score1}")
        print(feedback1)
        assert score1 >= 95, f"Score perfecto debería ser alto, obtuvo {score1}"
        assert "Remoción Tumor: 100.0%" in feedback1

        print("\n--- TEST 2: CIRUGÍA CON ERRORES ---")
        score2, feedback2 = run_pipeline(error_data)
        print(f"Score: {score2}")
        print(feedback2)
        # 100 - (5 * 5) - (25 * 1) = 100 - 25 - 25 = 50
        assert score2 <= 60, f"Score con errores debería ser bajo, obtuvo {score2}"
        assert "Lesión Tejido Sano: 5" in feedback2
        assert "Remoción Tumor: 40.0%" in feedback2
        
        print("\n✨ ¡Pruebas de scoring exitosas!")
        
    except Exception as e:
        print(f"\n❌ Error durante la prueba: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_pipeline_locally()
