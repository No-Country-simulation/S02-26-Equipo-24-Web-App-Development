let websocket = null;
let currentSurgeryId = null;

export function conectarWebSocket(token: string) {
    websocket = new WebSocket(`${process.env.WS_URL}/ws/simulation?token=${token}`);

    websocket.onopen = () => {
        console.log('🔌 WebSocket conectado - Simulación iniciada');
        // Enviar primer evento START
        enviarTelemetria(0, 0, 0, 'START');
    };

    websocket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        console.log('📨 Mensaje del servidor:', message);

        if (message.status === 'SAVED') {
            currentSurgeryId = message.surgeryId;
            console.log('💾 ¡Cirugía guardada!');
            console.log('📋 ID de la cirugía:', currentSurgeryId);

            // Guardar para consultar después
            localStorage.setItem('lastSurgeryId', currentSurgeryId);
        }
    };

    websocket.onerror = (error) => {
        console.error('❌ Error en WebSocket:', error);
    };

    websocket.onclose = () => {
        console.log('🔌 WebSocket desconectado');
    };
}