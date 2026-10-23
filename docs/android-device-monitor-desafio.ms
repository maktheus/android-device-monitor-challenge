.TL
Android Device Monitor
.br
Desafio técnico prático
.AU
Documento de orientação para desenvolvimento, entrega e defesa técnica
.AB
Desenvolva um aplicativo Android nativo capaz de observar o dispositivo, interpretar sinais de hardware e apresentar evidências em uma interface funcional.
.AE
.NH 1
1. Visão geral
.PP
O Android Device Monitor é um desafio de desenvolvimento Android para nível estagiário ou residente iniciante. A proposta combina fundamentos de Kotlin, Android SDK, ciclo de vida, interação com sensores, persistência de dados, testes e documentação.
.PP
Objetivo central: construir uma janela confiável para o hardware do dispositivo. O aplicativo deve mostrar dados reais, atualizar o acelerômetro em tempo real, registrar leituras e continuar utilizável quando um recurso estiver indisponível.
.PP
Contexto curricular: Núcleo Comum de Formação, com carga horária de 210 horas. Duração: cinco dias corridos de desenvolvimento e apresentação técnica de 30 minutos. Pontuação máxima: 100 pontos.
.NH 1
2. Stack e decisões técnicas
.IP "Linguagem" 15
Obrigatória: Kotlin.
.IP "Plataforma" 15
Obrigatória: Android nativo.
.IP "Interface" 15
Jetpack Compose ou XML com Views.
.IP "Build" 15
Gradle, com configuração documentada.
.IP "Arquitetura" 15
MVVM é recomendada. Outra organização é aceita mediante justificativa técnica.
.IP "Persistência" 15
Room é recomendada; SQLite é alternativa aceita.
.IP "Testes" 15
JUnit e pelo menos dois testes unitários básicos.
.IP "Versionamento" 15
Git com histórico de commits compreensível.
.PP
Separação de responsabilidades: a UI apresenta dados e recebe interações; o ViewModel gerencia estado e coordena ações; o Repository centraliza o acesso a sensores e persistência; os Data Sources encapsulam Android SDK e banco.
.NH 1
3. Requisitos funcionais
.NH 2
RF001 — Informações do dispositivo
.PP
Exibir fabricante, modelo, versão do Android e API Level usando dados reais do dispositivo, por exemplo por meio de android.os.Build. As informações devem ser legíveis e não podem estar fixas no código.
.NH 2
RF002 — Monitoramento de bateria
.PP
Apresentar percentual, estado de carregamento e fonte de alimentação quando disponível. Usar BatteryManager e/ou Intent.ACTION_BATTERY_CHANGED. Atualizar o estado quando necessário e informar dados indisponíveis.
.NH 2
RF003 — Monitoramento de acelerômetro
.PP
Capturar eixo X, Y, Z e timestamp usando SensorManager, Sensor e SensorEventListener. A tela deve permitir iniciar e parar o monitoramento, mostrar valores em tempo real e comunicar quando o dispositivo não possuir acelerômetro. Os listeners devem ser liberados no momento adequado do ciclo de vida.
.NH 2
RF004 — Histórico de leituras
.PP
Permitir salvar manualmente a leitura atual, listar registros, excluir registros e manter os dados após fechar ou reiniciar o aplicativo. Modelo mínimo: id Long, timestamp Long, axisX Float, axisY Float e axisZ Float.
.NH 2
RF005 — Navegação
.PP
Disponibilizar telas de informações do dispositivo, monitoramento e histórico. O botão voltar deve funcionar corretamente, sem travamentos ou perda inesperada de estado.
.NH 2
RF006 — Tratamento de erros
.PP
Tratar ausência de acelerômetro, falha ao salvar, histórico vazio e dados de bateria indisponíveis. Mensagens devem ser compreensíveis e a aplicação deve continuar utilizável.
.NH 1
4. Requisitos não funcionais
.IP "RNF001 — Desempenho" 20
A interface permanece responsiva durante leituras e persistência.
.IP "RNF002 — Gerenciamento de recursos" 20
Listeners não ficam ativos desnecessariamente em segundo plano.
.IP "RNF003 — Manutenibilidade" 20
Nomes claros e responsabilidades separadas.
.IP "RNF004 — Compatibilidade" 20
A versão mínima do Android é escolhida e documentada.
.IP "RNF005 — Segurança" 20
Nenhuma permissão desnecessária ou dado sensível sem justificativa.
.IP "RNF006 — Confiabilidade" 20
Falhas não provocam encerramentos inesperados.
.NH 1
5. Testes mínimos
.PP
Implemente no mínimo dois testes unitários. Cenários sugeridos: validar uma leitura de acelerômetro; formatar valores apresentados na interface; tratar valores inválidos; testar uma operação de histórico.
.PP
Entregue o código dos testes, o resultado da execução e uma breve descrição dos cenários.
.NH 1
6. Experimentação
.PP
O experimento deve demonstrar capacidade básica de coleta e interpretação de dados, sem necessidade de análise estatística avançada.
.IP 1.
Defina uma frequência ou estratégia de coleta e registre-a.
.IP 2.
Coloque o dispositivo em repouso e colete uma quantidade definida de amostras.
.IP 3.
Movimente o dispositivo de forma controlada e colete a mesma ordem de grandeza de amostras.
.IP 4.
Organize os resultados em tabela ou resumo.
.IP 5.
Compare os cenários e registre limitações, como orientação do aparelho, ruído e duração da coleta.
.PP
Condição A — dispositivo em repouso: espera-se variação menor e componente gravitacional predominante. Condição B — dispositivo em movimento: espera-se maior variação entre eixos e picos de aceleração. Não invente dados: identifique dispositivo, condições, duração e limitações.
.NH 1
7. Entregáveis
.IP "ENT001 — Repositório Git" 20
Código-fonte, histórico de commits, Gradle e README.
.IP "ENT002 — APK" 20
APK de debug instalável.
.IP "ENT003 — Documentação" 20
Descrição, requisitos, instruções de execução, arquitetura e limitações.
.IP "ENT004 — Testes" 20
Dois testes unitários e evidências de execução.
.IP "ENT005 — Relatório técnico" 20
Procedimento, resultados, comparação dos cenários e conclusão.
.IP "ENT006 — Demonstração" 20
Vídeo de até cinco minutos e apresentação técnica de 30 minutos.
.NH 1
8. Avaliação
.PP
Funcionalidades implementadas: 30 pontos. Fundamentos Android e Kotlin: 20 pontos. Arquitetura e qualidade de código: 15 pontos. Sensores e ciclo de vida: 15 pontos. Testes e tratamento de erros: 10 pontos. Documentação e Git: 5 pontos. Apresentação e domínio técnico: 5 pontos.
.PP
Faixas: 90–100 Excelente; 75–89 Bom; 60–74 Satisfatório; 0–59 Necessita desenvolvimento.
.NH 1
9. Desafios opcionais
.PP
Inteligência Artificial: exportar leituras em CSV e realizar análise exploratória simples. Segurança Android: analisar permissões e identificar riscos. Eficiência energética: comparar frequências de leitura e impactos no uso de recursos.
.NH 1
10. Critério de conclusão e checklist
.PP
O projeto está concluído quando as funcionalidades obrigatórias são demonstráveis, os artefatos são entregues e o candidato explica as decisões técnicas.
.IP \(bu 2
Projeto compila e o APK instala.
.IP \(bu 2
Informações, bateria, sensor e histórico funcionam.
.IP \(bu 2
Persistência sobrevive ao reinício.
.IP \(bu 2
Testes executam.
.IP \(bu 2
README e relatório explicam o caminho.
.IP \(bu 2
Candidato consegue defender as escolhas.
.PP
Android Device Monitor — Avaliação Técnica Prática — documento gerado para apresentação do desafio.
