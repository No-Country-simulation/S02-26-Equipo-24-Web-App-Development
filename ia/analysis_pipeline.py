import pandas as pd
import numpy as np


def run_pipeline(trajectory_data):

    movements = trajectory_data["movements"]

    df = pd.DataFrame([{
        "x": m["coordinates"][0],
        "y": m["coordinates"][1],
        "z": m["coordinates"][2] if len(m["coordinates"]) > 2 else 0,
        "event": m["event"],
        "timestamp": m["timestamp"]
    } for m in movements])

    df = df.sort_values("timestamp").reset_index(drop=True)

    df["time_s"] = (df["timestamp"] - df["timestamp"].iloc[0]) / 1000

    df["distance"] = np.sqrt(
        df["x"].diff()**2 +
        df["y"].diff()**2 +
        df["z"].diff()**2
    )

    df = df.fillna(0)

    tumor_touches = (df["event"] == "TUMOR_TOUCH").sum()
    hemorrhages = (df["event"] == "HEMORRHAGE").sum()

    score = 100
    score -= tumor_touches * 8
    score -= hemorrhages * 15
    score = max(0, min(100, score))

    feedback = f"""
Score final: {score}

Contactos con tumor: {tumor_touches}
Hemorragias: {hemorrhages}
Duración: {df["time_s"].iloc[-1]:.2f}s
"""

    return score, feedback.strip()