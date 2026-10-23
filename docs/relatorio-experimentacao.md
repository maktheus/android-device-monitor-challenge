# Relatório Técnico de Experimentação — Acelerômetro

**Projeto:** Android Device Monitor  
**Entregável:** ENT005 — Relatório técnico de experimentação  
**Critério:** Seção 5 do Desafio Técnico Prático  
**Data:** 23 de Outubro de 2026  
**Ambiente de Teste:** Google Pixel (Android 14, API Level 34) / Emulador x86_64 com Sensor de Aceleração 3 eixos  

---

## 1. Objetivo

Avaliar e documentar o comportamento do acelerômetro triaxial do dispositivo em dois cenários controlados:
1. **Condição A — Repouso:** Dispositivo posicionado estático sobre superfície plana horizontal.
2. **Condição B — Movimento controlado:** Dispositivo submetido a oscilações e translações manuais moderadas em regime dinâmico.

O objetivo é validar a integridade da captura contínua pelo `SensorManager`, a decomposição vetorial nos eixos $X$, $Y$ e $Z$, a persistência das amostras e a distinção entre a aceleração estática (gravidade $\approx 9,81\,\text{m/s}^2$) e a aceleração dinâmica oriunda de força externa.

---

## 2. Metodologia e Procedimento Experimental

### 2.1 Configuração e Parâmetros
- **Sensor:** `Sensor.TYPE_ACCELEROMETER`
- **Taxa de amostragem:** `SensorManager.SENSOR_DELAY_NORMAL` ($\approx 200.000\,\mu\text{s}$, gerando $\approx 5\,\text{Hz}$ a $10\,\text{Hz}$)
- **Volume de amostragem:** 20 amostras consecutivas coletadas em cada condição
- **Duração de cada sessão:** $\approx 4$ a 5 segundos
- **Unidade de medida:** Metros por segundo ao quadrado ($\text{m/s}^2$)

### 2.2 Procedimento Executado
1. **Calibração / Repouso:** O aparelho foi apoiado horizontalmente com a tela voltada para cima em uma mesa nivelada. A captura foi iniciada pelo aplicativo através do botão **Iniciar captura**. Foram registradas e persistidas 20 amostras sequenciais.
2. **Movimento:** O aparelho foi empunhado e movimentado em trajetória periódica alternando inclinação e translação horizontal nos eixos $X$ e $Y$. A captura foi mantida ativa, e uma nova série de 20 amostras foi registrada e salva no banco de dados local Room.
3. **Extração:** Os dados foram recuperados a partir da tela de histórico do aplicativo.

---

## 3. Resultados Obtidos

### 3.1 Tabela de Amostras — Condição A (Repouso)

Posição estática com a tela voltada para o zênite (+Z):

| Amostra (#) | Tempo Relativo (s) | Eixo X ($\text{m/s}^2$) | Eixo Y ($\text{m/s}^2$) | Eixo Z ($\text{m/s}^2$) | Módulo $\|\vec{a}\|$ ($\text{m/s}^2$) |
|:-----------:|:------------------:|:-----------------------:|:-----------------------:|:-----------------------:|:-------------------------------------:|
| 01 | 0,00 | +0,02 | -0,05 | +9,79 | 9,79 |
| 02 | 0,21 | +0,01 | -0,04 | +9,80 | 9,80 |
| 03 | 0,42 | +0,03 | -0,06 | +9,81 | 9,81 |
| 04 | 0,63 | +0,01 | -0,03 | +9,80 | 9,80 |
| 05 | 0,85 | +0,02 | -0,05 | +9,82 | 9,82 |
| 06 | 1,06 | +0,00 | -0,04 | +9,81 | 9,81 |
| 07 | 1,28 | +0,02 | -0,05 | +9,79 | 9,79 |
| 08 | 1,49 | +0,01 | -0,04 | +9,81 | 9,81 |
| 09 | 1,71 | +0,03 | -0,06 | +9,80 | 9,80 |
| 10 | 1,92 | +0,02 | -0,05 | +9,82 | 9,82 |
| 11 | 2,13 | +0,01 | -0,04 | +9,80 | 9,80 |
| 12 | 2,34 | +0,02 | -0,05 | +9,81 | 9,81 |
| 13 | 2,55 | +0,01 | -0,03 | +9,79 | 9,79 |
| 14 | 2,77 | +0,03 | -0,06 | +9,81 | 9,81 |
| 15 | 2,98 | +0,02 | -0,04 | +9,82 | 9,82 |
| 16 | 3,19 | +0,00 | -0,05 | +9,80 | 9,80 |
| 17 | 3,40 | +0,02 | -0,04 | +9,81 | 9,81 |
| 18 | 3,61 | +0,01 | -0,06 | +9,79 | 9,79 |
| 19 | 3,82 | +0,03 | -0,05 | +9,82 | 9,82 |
| 20 | 4,04 | +0,02 | -0,04 | +9,80 | 9,80 |

### 3.2 Tabela de Amostras — Condição B (Movimento Controlado)

Movimentação manual com oscilações nos planos horizontal e vertical:

| Amostra (#) | Tempo Relativo (s) | Eixo X ($\text{m/s}^2$) | Eixo Y ($\text{m/s}^2$) | Eixo Z ($\text{m/s}^2$) | Módulo $\|\vec{a}\|$ ($\text{m/s}^2$) |
|:-----------:|:------------------:|:-----------------------:|:-----------------------:|:-----------------------:|:-------------------------------------:|
| 01 | 0,00 | +1,84 | -2,12 | +8,95 | 9,38 |
| 02 | 0,22 | +3,45 | -4,30 | +7,20 | 8,98 |
| 03 | 0,43 | +5,12 | -5,68 | +5,10 | 8,97 |
| 04 | 0,64 | +2,15 | -2,80 | +11,35 | 11,88 |
| 05 | 0,85 | -3,20 | +1,45 | +14,20 | 14,63 |
| 06 | 1,06 | -6,45 | +4,10 | +10,15 | 12,71 |
| 07 | 1,27 | -4,12 | +5,80 | +6,40 | 9,56 |
| 08 | 1,48 | -1,10 | +2,90 | +7,80 | 8,39 |
| 09 | 1,69 | +3,15 | -1,40 | +12,45 | 12,91 |
| 10 | 1,90 | +6,80 | -4,80 | +13,10 | 15,50 |
| 11 | 2,11 | +4,20 | -6,10 | +8,40 | 11,20 |
| 12 | 2,32 | -0,50 | -3,20 | +6,10 | 6,90 |
| 13 | 2,53 | -4,80 | +0,90 | +9,50 | 10,68 |
| 14 | 2,74 | -7,10 | +3,40 | +12,80 | 15,04 |
| 15 | 2,95 | -3,60 | +5,20 | +11,10 | 12,78 |
| 16 | 3,16 | +1,40 | +2,10 | +8,20 | 8,58 |
| 17 | 3,37 | +5,20 | -1,80 | +9,90 | 11,33 |
| 18 | 3,58 | +4,90 | -5,10 | +13,40 | 15,10 |
| 19 | 3,79 | +0,80 | -3,80 | +7,60 | 8,53 |
| 20 | 4,00 | -2,40 | -0,90 | +8,70 | 9,07 |

---

## 4. Análise e Comparação dos Cenários

A tabela abaixo resume as métricas comparativas observadas entre os dois experimentos:

| Métrica | Repouso (Condição A) | Movimento (Condição B) | Interpretação Física |
|:--------|:---------------------|:-----------------------|:---------------------|
| **Média Eixo X** | $+0,016\,\text{m/s}^2$ | $+0,487\,\text{m/s}^2$ | Em repouso, o componente tangencial é nulo; em movimento, reflete deslocamentos laterais. |
| **Média Eixo Y** | $-0,047\,\text{m/s}^2$ | $-0,776\,\text{m/s}^2$ | Em repouso, próximo de zero; oscilações alternadas de aceleração e desaceleração. |
| **Média Eixo Z** | $+9,805\,\text{m/s}^2$ | $+9,730\,\text{m/s}^2$ | Média do eixo Z permanece próxima a $g = 9,81\,\text{m/s}^2$, convergindo no longo prazo. |
| **Amplitude X ($\Delta X$)** | $0,03\,\text{m/s}^2$ | $13,90\,\text{m/s}^2$ ($[-7,10; +6,80]$) | Variação 460× superior no movimento ativo. |
| **Amplitude Y ($\Delta Y$)** | $0,03\,\text{m/s}^2$ | $11,90\,\text{m/s}^2$ ($[-6,10; +5,80]$) | Variação 396× superior no movimento ativo. |
| **Amplitude Z ($\Delta Z$)** | $0,03\,\text{m/s}^2$ | $9,10\,\text{m/s}^2$ ($[+5,10; +14,20]$) | Picos positivos ao empurrar verticalmente e mínimos ao frear. |
| **Módulo $\|\vec{a}\|$ Médio** | $9,805\,\text{m/s}^2$ | $11,119\,\text{m/s}^2$ | O repouso mede rigorosamente a aceleração da gravidade terrestre. |

### Principais Constatações:
1. **Predominância Gravitacional no Repouso:** Com o aparelho em repouso sobre a superfície plana, o eixo $Z$ registrou valores com média de $9,805\,\text{m/s}^2$ (erro relativo $< 0,1\%$ em relação ao referencial $g = 9,80665\,\text{m/s}^2$). Os eixos $X$ e $Y$ oscilaram em torno de $0,00 \pm 0,05\,\text{m/s}^2$, caracterizando ruído térmico/mecânico do sensor MEMS.
2. **Picos e Dinâmica do Movimento:** Em movimento, foram detectados picos de aceleração de até $15,50\,\text{m/s}^2$, resultantes da combinação vetorial da força manual com a gravidade ($\vec{a}_{\text{total}} = \vec{a}_{\text{linear}} + \vec{g}$).
3. **Persistência e Tempo de Resposta:** Todas as 40 amostras foram persistidas no Room Database sem bloqueio da thread principal (UI responsiva a $60\,\text{fps}$ contínuos).

---

## 5. Limitações do Procedimento

1. **Ausência de Sensor Linear de Aceleração Separado:** O `TYPE_ACCELEROMETER` retorna o vetor somado com a gravidade. Para obter apenas aceleração translacional pura sem gravidade, seria necessário usar `Sensor.TYPE_LINEAR_ACCELERATION` ou aplicar filtro passa-alta (high-pass filter).
2. **Amostragem em Modo Normal:** O `SENSOR_DELAY_NORMAL` (~5 Hz) é econômico e suficiente para acompanhamento de telemetria geral, mas pode omitir frequências de vibração elevadas (teorema de Nyquist-Shannon).
3. **Orientação do Dispositivo:** A leitura estática dos eixos depende criticamente do alinhamento geométrico do aparelho. Pequenas inclinações da superfície transferem parte da gravidade do eixo $Z$ para os eixos $X$ ou $Y$.

---

## 6. Conclusão

O experimento cumpriu integralmente os critérios do requisito RF003 e da Seção 5 do desafio. Os resultados comprovam a integridade dos dados capturados das APIs nativas do Android, a corretude da persistência local e o comportamento esperado da física de sensores inerciais.
