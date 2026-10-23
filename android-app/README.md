# Android Device Monitor — aplicativo

Aplicativo Android nativo em Kotlin para cumprir o desafio descrito no site.

## Implementado

- Informações reais do fabricante, modelo, Android e API Level.
- Percentual, estado e fonte da bateria, com fallback para dados indisponíveis.
- Acelerômetro real com iniciar/parar, eixos X/Y/Z, timestamp e liberação no `onPause`.
- Histórico local com Room: salvar, listar, excluir e manter após reiniciar.
- Navegação entre Monitor, Dispositivo e Histórico.
- Tratamento para sensor ausente, histórico vazio e falha de persistência.
- Dois testes unitários para formatação de leituras e bateria.

## Executar

Abra esta pasta no Android Studio com Android SDK 35, JDK 17 e um emulador ou dispositivo API 26+ conectado. Depois sincronize o Gradle e execute a configuração `app` pelo botão Run. Em uma instalação que tenha o Gradle configurado, os mesmos passos podem ser executados por:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`.
