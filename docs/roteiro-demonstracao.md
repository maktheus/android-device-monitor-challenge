# Roteiro de Demonstração e Defesa Técnica

**Projeto:** Android Device Monitor  
**Entregável:** ENT006 — Vídeo de até 5 minutos e Apresentação técnica de 30 minutos  
**Critério:** Seção 6 e 7 do Desafio Técnico Prático  

---

## Parte 1: Roteiro do Vídeo de Demonstração (Até 5 Minutos)

Este roteiro organiza a gravação do vídeo prático apresentando o aplicativo em execução em dispositivo ou emulador Android.

### Estrutura Cronológica

| Tempo | Etapa | Foco da Demonstração | O que Mostrar na Tela |
|:-----:|:------|:---------------------|:----------------------|
| **00:00 - 00:45** | **Introdução e Contexto** | Apresentação pessoal, objetivo do desafio e stack técnica utilizada (Kotlin, Jetpack MVVM, Room, Sensors). | Tela inicial do app no emulador / dispositivo; visão geral do layout. |
| **00:45 - 01:30** | **RF001 & RF002: Dispositivo e Bateria** | Leitura de hardware real via `Build` e `BatteryManager`. Mapeamento para `BatteryStatus`. | Toque na aba **Dispositivo**: mostrar Fabricante, Modelo, Versão Android, API Level, Percentual da Bateria e Estado ("Carregando" / "Descarregando"). |
| **01:30 - 02:45** | **RF003: Acelerômetro em Tempo Real** | Demonstração da captura ao vivo dos eixos X, Y, Z, timestamp formatado e ciclo de vida. | Toque na aba **Monitor**: clicar em **Iniciar captura**; inclinar o emulador ou aparelho e mostrar valores mudando em tempo real; clicar em **Parar captura**. |
| **02:45 - 03:45** | **RF004: Histórico e Persistência Room** | Salvar leitura atual, persistir em banco local, listar histórico e excluir item. | Clicar em **Salvar leitura atual** (exibe toast "Leitura salva"); navegar para aba **Histórico**; mostrar o item com data/hora e valores; fechar e reabrir o app para provar persistência; excluir um item. |
| **03:45 - 04:30** | **RF005 & RF006: Navegação e Tratamento de Erros** | Navegação com botão "Voltar" (Back Dispatcher) e resiliência a estados excepcionais. | Pressionar o botão físico/gesto de "Voltar" a partir do Histórico ou Dispositivo e mostrar que retorna ao Monitor sem fechar o app. Mostrar mensagem de histórico vazio caso excluído tudo. |
| **04:30 - 05:00** | **Conclusão e Testes** | Breve menção aos testes unitários automatizados passando e encerramento. | Terminal com o resultado do `./gradlew testDebugUnitTest` aprovado e mensagem de encerramento. |

---

## Parte 2: Apresentação Técnica e Defesa (30 Minutos)

Roteiro completo para guiar os slides e a arguição técnica com a banca avaliadora.

### Bloco 1: Contexto e Decisões de Arquitetura (7 minutos)
- **Motivação:** Aplicação nativa offline, sem dependências externas desnecessárias, focada em observabilidade de hardware e ciclo de vida.
- **Padrão de Arquitetura (MVVM + Repository + Domain):**
  - **Data Layer:** `SensorRepository` encapsula `SensorManager`; `DeviceInfoRepository` encapsula `BatteryManager` e `Build`; `ReadingRepository` coordena `ReadingDao` e `AppDatabase` (Room).
  - **Domain:** Modelagem expressiva com `BatteryStatus` (enum de domínio) e imutabilidade nos dados de leitura.
  - **Presentation Layer:** `MonitorViewModel` com `StateFlow` expondo fluxos reativos; `MainActivity` coleta estados via `repeatOnLifecycle(Lifecycle.State.STARTED)` garantindo economia de recursos.

### Bloco 2: Gestão de Sensores e Ciclo de Vida (8 minutos)
- **Registro Consciente:** `SensorManager.registerListener` ativado apenas sob demanda explícita do usuário (`SENSOR_DELAY_NORMAL`).
- **Prevenção de Memory Leaks e Bateria Drenada:**
  - Desregistro obrigatório no `onPause()` da Activity e no `onCleared()` do ViewModel.
  - Acelerômetro não fica consumindo energia em background.
- **Tratamento de Ausência de Hardware:**
  - Checagem de `getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null`.
  - Caso nulo, a UI bloqueia os botões e informa ao usuário de forma transparente.

### Bloco 3: Persistência Local e Navegação (7 minutos)
- **Room Database:**
  - Tabela `accelerometer_readings` com chave primária auto-gerada e índices implícitos.
  - Queries reativas com Kotlin Coroutines `Flow<List<AccelerometerReading>>`.
  - Sobrevivência comprovada ao ciclo de vida e reinicialização.
- **Navegação Resiliente (RF005):**
  - Integração com `OnBackPressedDispatcher` e `OnBackPressedCallback`.
  - Navegação entre painéis com preservação integral de estado no ViewModel.

### Bloco 4: Qualidade, Testes e Experimentação (5 minutos)
- **Testes Unitários sem Dependência de Emulador:**
  - `ReadingFormatterTest`: formatação de números com precisão e sinal, timestamp UTC, fallback de bateria.
  - `BatteryStatusTest`: mapeamento correto dos códigos do Android SDK para enum.
  - `ReadingModelTest`: validação de conversão entre modelo de leitura e entidade de persistência.
- **Resultados da Experimentação (Repouso vs. Movimento):**
  - Análise dos dados coletados na Seção 5: componente estático de $\approx 9,81\,\text{m/s}^2$ no eixo Z em repouso vs. amplitudes de até $15,50\,\text{m/s}^2$ sob aceleração externa.

### Bloco 5: Perguntas e Respostas / Arguição Técnica (3 minutos)
- Perguntas prováveis da banca e defesas preparadas:
  1. *Por que usar Room e não SharedPreferences ou SQLite puro?*
     - Room oferece verificação de SQL em tempo de compilação, mapeamento de objetos sem boilerplate e suporte nativo a Coroutines Flow para atualização reativa da lista.
  2. *Como o app lida com dispositivos sem giroscópio ou acelerômetro?*
     - O repositório expõe `isAvailable` booleano. Se falso, a tela exibe aviso e desabilita interações que causariam exceção, garantindo estabilidade (RNF006).
  3. *Qual é a versão mínima do Android suportada?*
     - Android 8.0 (API 26), cobrindo mais de 96% dos dispositivos ativos sem exigir branches condicionais legados complexos.
