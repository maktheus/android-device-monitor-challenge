# Android Device Monitor — desafio técnico

Site de apresentação do desafio prático para desenvolver um aplicativo Android nativo em Kotlin, com foco em informações do dispositivo, bateria, acelerômetro, persistência e qualidade de engenharia.

## Abrir localmente

O site é estático. Abra `index.html` no navegador ou sirva a pasta com qualquer servidor local:

```bash
npx serve .
```

O documento completo está disponível em `docs/android-device-monitor-desafio.pdf` e também na versão HTML em `docs/android-device-monitor-desafio.html`. O roteiro técnico com referências oficiais e passo a passo está em `docs/guia-implementacao.md`.

## Publicar no GitHub Pages

1. Crie um repositório no GitHub e envie o conteúdo desta pasta para a branch `main`.
2. Em **Settings → Pages**, selecione **GitHub Actions** como origem.
3. O workflow `.github/workflows/deploy-pages.yml` fará o deploy a cada push.

O site não usa build step, banco, servidor ou conexão externa para funcionar. As fontes do Google são apenas um aprimoramento visual; todo o conteúdo principal continua disponível offline.
