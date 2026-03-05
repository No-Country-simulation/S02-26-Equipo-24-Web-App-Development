import pandas as pd
import numpy as np
from typing import Dict, Tuple, List

def run_pipeline(trajectory_data: Dict) -> Tuple[float, str]:
    """
    PIPELINE COMPLETO DE ANÁLISIS DE 5 PASOS
    """
    # PASO 1: Ingesta y Limpieza
    df = _paso1_ingestar_y_limpiar(trajectory_data)
    
    # PASO 2: Métricas de Destreza (Física)
    metricas = _paso2_calcular_destreza(df)
    
    # PASO 3: Comparación con Patrón Oro
    benchmarking = _paso3_benchmarking(df)
    
    # PASO 4: Análisis de Riesgo
    riesgo = _paso4_analizar_riesgo(df)
    
    # PASO 5: Feedback Inteligente
    score, feedback = _paso5_generar_feedback(metricas, benchmarking, riesgo)
    
    return score, feedback

def _paso1_ingestar_y_limpiar(trajectory_data: Dict) -> pd.DataFrame:
    movements = trajectory_data["movements"]
    df = pd.DataFrame([{
        "x": m["coordinates"][0],
        "y": m["coordinates"][1],
        "z": m["coordinates"][2] if len(m["coordinates"]) > 2 else 0,
        "event": m["event"],
        "timestamp": m["timestamp"]
    } for m in movements])
    
    df = df.sort_values("timestamp").reset_index(drop=True)
    # Tiempo relativo en segundos
    df["t"] = (df["timestamp"] - df["timestamp"].iloc[0]) / 1000.0
    df["dt"] = df["t"].diff().fillna(0)
    
    return df

def _paso2_calcular_destreza(df: pd.DataFrame) -> Dict:
    # Diferencias de posición
    dx = df["x"].diff().fillna(0)
    dy = df["y"].diff().fillna(0)
    dz = df["z"].diff().fillna(0)
    dist = np.sqrt(dx**2 + dy**2 + dz**2)
    
    # Velocidad (v = ds/dt)
    v = dist / df["dt"].replace(0, np.inf)
    v = v.replace(np.inf, 0)
    
    # Aceleración (a = dv/dt)
    a = v.diff() / df["dt"].replace(0, np.inf)
    a = a.replace(np.inf, 0)
    
    # Jerk (j = da/dt) - Suavidad
    j = a.diff() / df["dt"].replace(0, np.inf)
    j = j.replace(np.inf, 0)
    
    # Economía de movimiento
    total_dist = dist.sum()
    p1 = np.array([df["x"].iloc[0], df["y"].iloc[0], df["z"].iloc[0]])
    p2 = np.array([df["x"].iloc[-1], df["y"].iloc[-1], df["z"].iloc[-1]])
    direct_dist = np.linalg.norm(p2 - p1)
    economia = total_dist / direct_dist if direct_dist > 0 else 1.0
    
    return {
        "economia": economia,
        "v_avg": v.mean(),
        "a_max": a.abs().max(),
        "j_avg": j.abs().mean(),
        "total_dist": total_dist,
        "duration": df["t"].iloc[-1]
    }

def _paso3_benchmarking(df: pd.DataFrame) -> Dict:
    # Benchmarking simple: Desviación vs Línea Recta Ideal
    p_start = np.array([df["x"].iloc[0], df["y"].iloc[0], df["z"].iloc[0]])
    p_end = np.array([df["x"].iloc[-1], df["y"].iloc[-1], df["z"].iloc[-1]])
    
    def dist_to_line(p, a, b):
        if np.all(a == b): return np.linalg.norm(p-a)
        return np.linalg.norm(np.cross(b-a, a-p)) / np.linalg.norm(b-a)
    
    desviaciones = [dist_to_line(np.array([r.x, r.y, r.z]), p_start, p_end) for r in df.itertuples()]
    
    return {
        "desviacion_avg": np.mean(desviaciones),
        "precision": max(0, 100 - np.mean(desviaciones) * 10)
    }

def _paso4_analizar_riesgo(df: pd.DataFrame) -> Dict:
    tumor_removals = (df["event"] == "TUMOR_REMOVAL").sum()
    hemorrhages = (df["event"] == "HEMORRHAGE").sum()
    kidney_touches = (df["event"] == "KIDNEY_TOUCH").sum()
    
    # Análisis de cuadrantes (donde hubo complicaciones)
    mid_x = (df["x"].max() + df["x"].min()) / 2
    mid_y = (df["y"].max() + df["y"].min()) / 2
    
    problemas = df[df["event"].isin(["KIDNEY_TOUCH", "HEMORRHAGE"])]
    cuadrantes_criticos = []
    if not problemas.empty:
        for p in problemas.itertuples():
            pos = ""
            pos += "Sup" if p.y > mid_y else "Inf"
            pos += "-Der" if p.x > mid_x else "-Izq"
            if pos not in cuadrantes_criticos: cuadrantes_criticos.append(pos)
            
    return {
        "removals": int(tumor_removals),
        "hemorrhages": int(hemorrhages),
        "kidney_touches": int(kidney_touches),
        "cuadrantes": cuadrantes_criticos
    }

def _paso5_generar_feedback(m: Dict, b: Dict, r: Dict) -> Tuple[float, str]:
    score = 100.0
    # Penalizaciones (Basadas en técnica y seguridad)
    score -= r["kidney_touches"] * 5
    score -= r["hemorrhages"] * 25
    if m["economia"] > 1.8: score -= 10
    
    # Bonificación por cumplimiento (si removió gran parte del tumor)
    # Suponiendo que hay 25 fragmentos originalmente
    progreso_tumor = (r["removals"] / 25.0) * 100
    
    score = max(0, min(100, score))
    
    status = "🌟 EXCELENTE" if score >= 90 else "✅ BUENO" if score >= 75 else "⚠️ MEJORABLE" if score >= 60 else "❌ DEFICIENTE"
    
    feedback = f"""{status} | Score: {score:.1f}/100

ALERTAS CRÍTICAS
· Hemorragias: {r["hemorrhages"]} {"(REVISAR TÉCNICA)" if r["hemorrhages"] > 0 else "(Ninguna)"}
· Lesión Tejido Sano: {r["kidney_touches"]}
· Cuadrantes de Riesgo: {", ".join(r["cuadrantes"]) if r["cuadrantes"] else "Ninguno"}

MÉTRICAS DE DESTREZA
· Remoción Tumor: {progreso_tumor:.1f}%
· Economía: {m["economia"]:.2f}x (Ideal < 1.2x)
· Fluidez (Jerk): {m["j_avg"]:.2f} 
· Precisión Path: {b["precision"]:.1f}%

ESTADÍSTICAS
· Duración: {m["duration"]:.1f}s
· Distancia: {m["total_dist"]:.2f}u
· Velocidad: {m["v_avg"]:.2f}u/s

RECOMENDACIONES
"""
    if r["hemorrhages"] > 0: feedback += "· Priorizar control vascular en cuadrantes críticos.\n"
    if r["kidney_touches"] > 3: feedback += "· Mantener mayor distancia de los márgenes de tejido sano.\n"
    if m["economia"] > 1.8: feedback += "· Planificar trayectorias más directas para reducir fatiga.\n"
    if b["precision"] < 70: feedback += "· Mantener mayor estabilidad en la ejecución del path ideal.\n"
    if progreso_tumor < 70: feedback += "· Asegurar la remoción total de fragmentos identificados.\n"
    
    return score, feedback.strip()
