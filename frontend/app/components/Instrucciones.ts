import * as GUI from "@babylonjs/gui";

export function mostrarInstrucciones() {
    const advancedTexture = GUI.AdvancedDynamicTexture.CreateFullscreenUI("UI");

    // PANEL DE INSTRUCCIONES
    const instructionsPanel = new GUI.Rectangle();
    instructionsPanel.width = "320px";
    instructionsPanel.height = "180px";
    instructionsPanel.cornerRadius = 15;
    instructionsPanel.color = "black";
    instructionsPanel.thickness = 2;
    instructionsPanel.background = "rgba(220, 255, 220, 0.9)";
    instructionsPanel.paddingTop = "10px";
    instructionsPanel.paddingRight = "20px";
    instructionsPanel.horizontalAlignment =
        GUI.Control.HORIZONTAL_ALIGNMENT_RIGHT;
    instructionsPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_TOP;

    advancedTexture.addControl(instructionsPanel);

    // StackPanel para organizar el texto de instrucciones
    const instructionsStack = new GUI.StackPanel();
    instructionsStack.paddingTop = "10px";
    instructionsStack.paddingLeft = "10px";
    instructionsStack.paddingRight = "10px";

    instructionsPanel.addControl(instructionsStack);

    // Título de instrucciones
    const instructionsTitle = new GUI.TextBlock();
    instructionsTitle.text = "Instrucciones de Cirugía";
    instructionsTitle.height = "30px";
    instructionsTitle.color = "black";
    instructionsTitle.fontSize = 18;
    instructionsTitle.fontStyle = "bold";
    instructionsTitle.textHorizontalAlignment =
        GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    instructionsStack.addControl(instructionsTitle);

    const instructionsText = new GUI.TextBlock();
    instructionsText.text =
        "1. Presiona INICIAR CIRUGÍA.\n" +
        "2. Usa el mouse para mover el bisturí.\n" +
        "3. Mantén click para cortar el tumor.\n" +
        "4. Evita cortar la arteria roja.\n" +
        "5. Presiona TERMINAR CIRUGÍA al finalizar.";

    instructionsText.color = "black";
    instructionsText.fontSize = 14;
    instructionsText.paddingBottom = "10px";
    instructionsText.textWrapping = true;
    instructionsText.resizeToFit = true;
    instructionsText.textHorizontalAlignment =
        GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    instructionsStack.addControl(instructionsText);
}