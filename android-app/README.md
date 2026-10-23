# Android Device Monitor — Aplicativo Nativo

Aplicativo Android nativo desenvolvido em Kotlin para atender integralmente ao desafio prático de monitoramento de dispositivo, telemetria inercial e persistência local.

---

## 1. Arquitetura e Decisões Técnicas

O projeto segue as recomendações oficiais do Google Android Architecture Components com padrão **MVVM (Model-View-ViewModel)**, **Repository Pattern** e fluxo unidirecional de dados:

```text
android-app/app/src/main/java/com/example/androiddevicemonitor/
├── data/
│   ├── AccelerometerReading.kt    # Entidade Room e modelo imutável LiveReading
│   ├── AppDatabase.kt             # RoomDatabase com migração segura e singleton
│   ├── BatteryStatus.kt           # Enum de domínio para estados de energia
│   ├── DeviceInfoRepository.kt    # Leitura de Build e parsing de BatteryManager
│   ├── ReadingDao.kt              # DAO para observação reativa, inserção e exclusão
│   ├── ReadingFormatter.kt        # Formatação determinística de eixos, datas e bateria
│   ├── ReadingRepository.kt       # Interface e implementação de repositório de persistência
│   └── SensorRepository.kt        # SensorEventListener e gerenciador de ciclo de vida do acelerômetro
└── ui/
    ├── MainActivity.kt            # Activity com OnBackPressedCallback, abas e observação de ciclo de vida
    └── MonitorViewModel.kt        # ViewModel com StateFlows reativos e coordenação de ações
```

---

## 2. Requisitos Implementados

| Requisito | Descrição | Implementação |
|:----------|:----------|:--------------|
| **RF001** | Informações do Dispositivo | Consulta real de `Build.MANUFACTURER`, `Build.MODEL`, `Build.VERSION.RELEASE` e `Build.VERSION.SDK_INT`. |
| **RF002** | Monitoramento de Bateria | `BatteryManager` via `ACTION_BATTERY_CHANGED`, mapeado para enum de domínio `BatteryStatus` (`Charging`, `Discharging`, `Full`, `NotCharging`, `Unavailable`) e identificação da fonte de energia (AC, USB, Sem Fio). |
| **RF003** | Acelerômetro em Tempo Real | Captura dos eixos X, Y e Z com `SensorManager`, registro e liberação automática no ciclo de vida (`onPause`/`onCleared`). |
| **RF004** | Histórico com Persistência | Banco local SQLite gerenciado pelo Jetpack Room, com inserção, listagem reativa via Kotlin Coroutines `Flow` e exclusão individual. Os dados sobrevivem ao reinício do app. |
| **RF005** | Navegação | Interface com 3 painéis (Monitor, Dispositivo, Histórico) e suporte completo ao botão "Voltar" (`OnBackPressedCallback`) retornando à tela principal sem perda de estado. |
| **RF006** | Tratamento de Erros | Indicação imediata de acelerômetro indisponível com desativação de botões, tratamento de histórico vazio, fallbacks de bateria e mensagens informativas em Toasts. |

---

## 3. Gestão de Ciclo de Vida e Recursos (RNF001 a RNF006)

- **Sensibilidade a Background:** O listener do acelerômetro é desligado no `onPause()` da Activity e no `onCleared()` do ViewModel, garantindo consumo zero de bateria quando o app não estiver visível.
- **Coleta Segura:** A UI coleta os fluxos utilizando `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- **Compatibilidade:** `minSdk = 26` (Android 8.0 Oreo), `targetSdk = 35` (Android 15), compatível com qualquer emulador ou aparelho moderno.
- **Privacidade e Segurança:** 100% offline, sem permissões privilegiadas ou envio de dados.

---

## 4. Testes Unitários

A suíte de testes locais em `app/src/test/` não depende de emuladores e valida o núcleo da lógica:

1. **`ReadingFormatterTest`**:
   - Formatação com precisão de 2 casas decimais e sinais explícitos (+/-).
   - Formatação determinística de timestamp em formato `dd/MM/yyyy HH:mm:ss` em UTC.
   - Formatação de bateria e tratamento de valores nulos e extremos.
   - Rótulos do enum de domínio `BatteryStatus`.
2. **`BatteryStatusTest`**:
   - Mapeamento correto das constantes inteiras do Android `BatteryManager` para o enum de domínio.
   - Fallback robusto de estados nulos ou códigos desconhecidos para `UNAVAILABLE`.
3. **`ReadingModelTest`**:
   - Conversão de modelo transitório `LiveReading` para entidade persistente `AccelerometerReading`.
   - Valores padrão de leitura zerada.

---

## 5. Como Executar

### Pré-requisitos
- JDK 17
- Android SDK 35 (com Build Tools e Platform Tools)
- Dispositivo ou emulador Android com API 26+

### Comandos de Build e Teste

Utilize o Gradle Wrapper fornecido na raiz de `android-app/`:

```bash
# Executar a suíte de testes unitários
./gradlew testDebugUnitTest

# Gerar o APK de Debug
./gradlew assembleDebug
```

O arquivo gerado estará em:
```text
app/build/outputs/apk/debug/app-debug.apk
```

Para instalar diretamente no aparelho conectado via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.androiddevicemonitor/.ui.MainActivity
```

---

## 6. Documentação Complementar

- **[Relatório Técnico do Experimento](../docs/relatorio-experimentacao.md)**: Análise detalhada do acelerômetro em repouso vs. movimento com tabelas de medições e limitações físicas.
- **[Roteiro de Demonstração e Apresentação](../docs/roteiro-demonstracao.md)**: Roteiros cronometrados para o vídeo de 5 minutos e apresentação de 30 minutos.
