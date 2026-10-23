# Guia de implementação — Android Device Monitor

Este guia acompanha o desafio e serve como roteiro de execução. Ele não define uma única solução: a avaliação considera se a solução é correta, explicável, testável e adequada ao escopo.

## 1. Comece pelo contrato do produto

Antes de abrir o Android Studio, traduza os requisitos em três telas e estados explícitos:

1. **Monitoramento:** captura parada, captura ativa, sensor ausente e falha de leitura.
2. **Dispositivo:** dados do fabricante/modelo, versão do Android, API Level e estado da bateria.
3. **Histórico:** lista vazia, lista preenchida, salvamento bem-sucedido, falha e exclusão.

Para cada estado, escreva: o que aparece, qual ação o usuário pode realizar e qual mensagem deve ser exibida. Essa tabela reduz decisões implícitas e facilita os testes.

## 2. Escolha a estrutura do projeto

Uma organização simples e suficiente para o desafio:

```text
app/src/main/java/.../
├── data/
│   ├── local/       # Entity, DAO e RoomDatabase
│   ├── sensor/      # SensorManager e adapter do acelerômetro
│   └── repository/  # coordena fontes de dados
├── domain/          # modelos e regras sem dependência de UI
├── presentation/
│   ├── monitor/
│   ├── device/
│   └── history/
└── MainActivity.kt
```

O Android recomenda separar UI e dados, manter o estado fora de `Activity`/`Fragment`, usar uma fonte única da verdade e dirigir a interface por modelos de estado. Consulte a [guia oficial de arquitetura Android](https://developer.android.com/topic/architecture) e as [recomendações de arquitetura](https://developer.android.com/topic/architecture/recommendations).

## 3. Implemente em ordem de risco

### Passo A — criar o projeto

- Use Kotlin, Gradle e uma versão mínima documentada no README.
- Escolha Jetpack Compose ou XML com Views; não misture tecnologias sem necessidade.
- Confirme primeiro que o projeto compila e instala em um dispositivo/emulador.
- Mantenha o app offline: as funcionalidades obrigatórias não dependem de rede.

Referências: [Kotlin — primeiros passos](https://kotlinlang.org/docs/getting-started.html), [Kotlin Android](https://developer.android.com/kotlin) e [Android Studio](https://developer.android.com/studio).

### Passo B — ler informações do dispositivo

Crie um modelo de apresentação, por exemplo `DeviceInfo`, e preencha-o a partir de `Build.MANUFACTURER`, `Build.MODEL`, `Build.VERSION.RELEASE` e `Build.VERSION.SDK_INT`. A UI deve consumir o modelo; não espalhe chamadas de `Build` por vários componentes.

Referências: [`Build`](https://developer.android.com/reference/android/os/Build) e [`Build.VERSION`](https://developer.android.com/reference/android/os/Build.VERSION).

### Passo C — consultar a bateria

Use o sticky broadcast `Intent.ACTION_BATTERY_CHANGED` para uma leitura inicial e trate extras ausentes antes de formatar o resultado. Converta o status para uma enumeração de domínio, como `Charging`, `Discharging`, `Full` e `Unavailable`, em vez de exibir constantes diretamente na UI.

Referência: [`BatteryManager`](https://developer.android.com/reference/android/os/BatteryManager).

### Passo D — conectar o acelerômetro

1. Obtenha o `SensorManager` pelo contexto apropriado.
2. Procure `getDefaultSensor(Sensor.TYPE_ACCELEROMETER)`.
3. Se o retorno for `null`, exponha o estado `Unavailable` e uma mensagem útil.
4. Registre um `SensorEventListener` ao iniciar a captura.
5. Copie `values[0]`, `values[1]`, `values[2]` e `timestamp` para um modelo imutável.
6. Pare o listener ao interromper a captura e no `onPause`/equivalente do ciclo de vida.
7. Use uma taxa de amostragem suficiente para a demonstração, sem manter o sensor ativo desnecessariamente.

O [guia oficial de sensores](https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview) explica `SensorManager`, `Sensor`, `SensorEvent` e `SensorEventListener`. A referência de [`SensorManager`](https://developer.android.com/reference/android/hardware/SensorManager) reforça que sensores devem ser desabilitados quando a tela é pausada, pois o sistema não os desliga automaticamente em todos os casos.

### Passo E — persistir leituras

Modele a entidade `AccelerometerReading` com `id`, `timestamp`, `axisX`, `axisY` e `axisZ`. Crie um DAO com operações para inserir, observar/listar e excluir. Encapsule a base em um `RoomDatabase` e exponha o acesso por um repositório.

O [guia de Room](https://developer.android.com/training/data-storage/room) documenta `Entity`, `Dao` e `RoomDatabase`, além da verificação de consultas em tempo de compilação. O [guia de armazenamento local](https://developer.android.com/training/data-storage) ajuda a justificar por que uma base privada local atende ao requisito offline e não exige permissão de armazenamento.

### Passo F — conectar estado e interface

Faça o `ViewModel` expor um único estado de tela, por exemplo `MonitorUiState`, contendo leitura atual, status da captura, erro e disponibilidade do sensor. A UI apenas renderiza esse estado e envia eventos como `StartMonitoring`, `StopMonitoring` e `SaveReading`.

Referências: [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [fluxo unidirecional de dados](https://developer.android.com/topic/architecture/ui-layer) e [ciclo de vida](https://developer.android.com/topic/libraries/architecture/lifecycle).

## 4. Teste o que não pode falhar

Comece com testes unitários que não dependam de `SensorManager` real:

- validar que uma leitura com três eixos e timestamp vira o modelo esperado;
- formatar números e timestamp para apresentação;
- mapear valores inválidos ou indisponíveis para um estado de erro compreensível;
- testar o repositório usando um fake ou uma implementação controlada.

Coloque testes locais em `app/src/test/`. A documentação oficial diferencia [testes locais no JVM](https://developer.android.com/training/testing/local-tests) de [testes instrumentados](https://developer.android.com/training/testing/instrumented-tests) e explica o que deve ser coberto em [What to test](https://developer.android.com/training/testing/fundamentals/what-to-test).

## 5. Execute a experimentação

Registre a versão do aparelho, a frequência escolhida, o número de amostras e a duração. Colete uma série em repouso e outra em movimento. Compare amplitude, estabilidade e picos; não transforme um conjunto pequeno de leituras em uma conclusão estatística forte. Inclua limitações: orientação do aparelho, ruído, forma do movimento e tempo de coleta.

## 6. Roteiro de cinco dias

| Dia | Resultado mínimo |
| --- | --- |
| 1 | Projeto compila; navegação inicial e informações do dispositivo funcionam. |
| 2 | Bateria e acelerômetro funcionam; estados de indisponibilidade estão previstos. |
| 3 | Histórico local funciona; leitura, exclusão e reinício foram verificados. |
| 4 | Testes, tratamento de erros, revisão de ciclo de vida e experimento concluídos. |
| 5 | README, relatório, APK, vídeo e apresentação revisados; demonstração ensaiada. |

## 7. Checklist técnico antes da entrega

- [ ] Nenhum valor obrigatório é simulado.
- [ ] O listener do acelerômetro é registrado e removido em momentos corretos.
- [ ] O app informa quando não há acelerômetro ou quando a bateria não pode ser lida.
- [ ] A leitura salva sobrevive ao fechamento e à reabertura do app.
- [ ] A UI continua responsiva durante leitura e persistência.
- [ ] Existem pelo menos dois testes unitários executáveis.
- [ ] O README registra versão mínima, build, arquitetura, limitações e comandos.
- [ ] O relatório separa procedimento, resultados, comparação e conclusão.

## Referências oficiais reunidas

- [Android Developers — arquitetura](https://developer.android.com/topic/architecture)
- [Android Developers — sensores](https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview)
- [Android Developers — `SensorManager`](https://developer.android.com/reference/android/hardware/SensorManager)
- [Android Developers — `Build`](https://developer.android.com/reference/android/os/Build)
- [Android Developers — `BatteryManager`](https://developer.android.com/reference/android/os/BatteryManager)
- [Android Developers — Room](https://developer.android.com/training/data-storage/room)
- [Android Developers — testes locais](https://developer.android.com/training/testing/local-tests)
- [Kotlin — documentação](https://kotlinlang.org/docs/)
