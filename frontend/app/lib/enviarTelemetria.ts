export function enviarTelemetria(x: any, y: any, z: any, evento = 'NONE') {
    const telemetry = {
        coordinates: [x, y, z],
        event: evento,  // 'START', 'NONE', 'TUMOR_TOUCH', 'HEMORRHAGE', 'FINISH'
        timestamp: Date.now()
    };

    console.log('📤 Enviando:', telemetry);
    websocket.send(JSON.stringify(telemetry));
}