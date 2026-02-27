from client import JustinaAIClient
from analysis_pipeline import run_pipeline


def main():
    surgery_id = "123e4567-e89b-12d3-a456-426614174000"

    client = JustinaAIClient()
    client.login()

    data = client.get_trajectory(surgery_id)

    if not data:
        print("No se pudo obtener la cirugía")
        return

    score, feedback = run_pipeline(data)

    client.send_analysis(surgery_id, score, feedback)


if __name__ == "__main__":
    main()