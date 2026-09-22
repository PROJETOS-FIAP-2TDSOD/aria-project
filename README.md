# ARIA — Plataforma de Gestão de Inovação Corporativa

Documentação técnica do aplicativo Android. Reflete o estado do repositório ao
final da **Sprint 2 — Challenge Águia Branca (FIAP)**, com o backend real
integrado (substituindo o mock da Sprint 1) e hospedado na nuvem.

---

## 1. Visão geral

| Item | Detalhe |
|------|---------|
| **Nome** | ARIA (Innovation Management System) |
| **Contexto** | FIAP Challenge em parceria com **Grupo Águia Branca** |
| **Objetivo** | Integrar estratégia, pessoas e execução no ciclo de inovação: ideias → análise → projetos → orientações → mensuração (ROI) |
| **Package** | `com.fiap.ariachallenge` |
| **Application ID** | `com.fiap.ariachallenge` |
| **Sprint atual** | **Sprint 2** — app nativo Android integrado a um backend real (Spring Boot + MongoDB Atlas) |
| **Backend** | `aria-backend` (repositório irmão), hospedado no **Render** — `https://aria-backend-p7bk.onrender.com` |

### Estado do projeto

**Integração completa com backend real** na Sprint 2:

- UI completa em Jetpack Compose para **Operador**, **Gestor** e **Líder**
- API REST **real** (Retrofit + Spring Boot), autenticação JWT de verdade
- CRUD completo de ideias, projetos e orientações consumindo o backend
- IA real (Google Gemini) para pontuação de ideias (`ai-score`)
- Dashboard do líder consumindo endpoints de relatório reais
- Notificações reais (leitura, marcar como lida)
- Persistência local via **DataStore** apenas para sessão/token e avatar (o
  restante dos dados de negócio vem sempre da API)
- Sem Firebase, Supabase ou Room

> A Sprint 1 (backend 100% mockado) está preservada no histórico do Git;
> esta versão do README documenta o estado pós-integração.

---

## 2. Alinhamento ao enunciado da Sprint 2

| Requisito | Status | Observação |
|---|---|---|
| Login 3 perfis, JWT real | ✅ | `AuthRepositoryImpl` |
| Orientações: CRUD do líder, consulta dos demais | ✅ | `OrientationRepositoryImpl` |
| Ideias: CRUD do operador (inclusive exclusão) | ✅ | `IdeaRepositoryImpl` |
| Ideias: gestor prioriza/aprova, vincula à estratégia | ✅ | `AnalisarIdeiaViewModel` |
| Projetos: CRUD do gestor, progresso/resultados | ✅ | `ProjectRepositoryImpl` |
| Projetos: líder consulta (sem criar/editar) | ✅ | Acesso restrito por navegação + backend `@PreAuthorize` |
| Dashboard do líder a partir de endpoints de relatório | ✅ | `IDashboardRepository` → `/dashboard/summary`, `/roi-by-project`, `/roi-by-strategy` |
| Diferencial de IA (pontuação de ideias) | ✅ | Google Gemini via `ai-score`, GESTOR |
| Backend hospedado / não depende de ambiente local | ✅ | Render + MongoDB Atlas |

---

## 3. Perfis de usuário

| Perfil | `UserRole` | Responsabilidades |
|--------|------------|-------------------|
| **Operador** | `OPERADOR` | Home, orientações (leitura), minhas ideias (CRUD completo), nova ideia, detalhes, notificações, perfil, gamificação |
| **Gestor** | `GESTOR` | Home, pendentes, analisar ideia (aprovar/rejeitar/pontuar via IA), projetos (CRUD), orientações (leitura), notificações, perfil |
| **Líder** | `LIDER` | Dashboard, orientações (CRUD), análises, tendências, projetos (consulta), notificações, perfil, export PDF |

Registro de novas contas cria sempre perfil **OPERADOR** e redireciona automaticamente para o home após sucesso.

---

## 4. Stack técnica

| Tecnologia | Versão (fonte: `gradle/libs.versions.toml`) |
|------------|---------------------------------------------|
| **Kotlin** | 2.1.20 |
| **Android Gradle Plugin** | 8.9.1 |
| **Gradle Wrapper** | 9.2.1 |
| **compileSdk / targetSdk** | 36 |
| **minSdk** | 26 |
| **JVM** | 11 |

### Bibliotecas principais

| Biblioteca | Versão |
|------------|--------|
| Jetpack Compose BOM | 2025.07.00 |
| Material 3 + adaptive navigation suite | via BOM |
| Navigation Compose | 2.9.0 |
| Lifecycle (runtime, ViewModel) | 2.10.0 |
| Activity Compose | 1.13.0 |
| **Hilt** | 2.51.1 |
| Hilt Navigation Compose | 1.2.0 |
| KSP | 2.1.20-1.0.32 |
| DataStore Preferences | 1.1.7 |
| Retrofit | 2.11.0 |
| OkHttp (+ logging) | 4.12.0 |
| Gson | 2.11.0 |
| **Coil** (avatar) | 2.7.0 |
| Google Fonts (Outfit, IBM Plex Sans) | 1.7.8 |

### Não utilizado

Room, Firebase, Supabase.

---

## 5. Arquitetura

### Padrão

**MVVM** + **Clean-ish layering**:

```
ui/           → Compose screens, design system Aria, navegação por perfil
domain/       → modelos, analytics, gamificação, interfaces de repositório
data/         → *RepositoryImpl (real), remote (API, DTOs, mappers)
di/           → Hilt (RepositoryModule, NetworkModule)
util/         → localização Compose, formatação, parsers
export/       → AriaAnalyticsPdfExporter
```

ViewModels chamam repositórios diretamente (sem camada `usecase/`).

### Injeção de dependência

- `@HiltAndroidApp` — `AriaApplication`
- `@AndroidEntryPoint` — `MainActivity`
- `@HiltViewModel` — ViewModels
- `RepositoryModule` — binds `*RepositoryImpl` → `I*Repository`
- `NetworkModule` — OkHttp, Retrofit, `AriaApiService`, `BASE_URL`

### State management

- `MutableStateFlow` / `StateFlow` nos ViewModels
- UI: `collectAsState()`
- Listas: `Flow` de leitura única por chamada de API (com `.catch{}` pra não
  derrubar a tela em erro de rede)
- Telas com ação de criar/editar/excluir usam `DisposableEffect` +
  `Lifecycle.Event.ON_RESUME` para recarregar ao voltar de navegação
  (`popBackStack` mantém a mesma instância de ViewModel viva, então o
  `init { load() }` não roda de novo sozinho)
- Navegação one-shot: lambdas `onNavigate` / `LaunchedEffect`

### Diagrama simplificado

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐
│   Screen    │────▶│  ViewModel   │────▶│  I*Repository   │
└─────────────┘     └──────────────┘     └────────┬────────┘
                                                    │
                    ┌───────────────────────────────┴───────────────────────────────┐
                    ▼                               ▼                               ▼
              AuthRepositoryImpl              IdeaRepositoryImpl              ProjectRepositoryImpl
                    │                               │                               │
                    └───────────────────────────────┼───────────────────────────────┘
                                                    ▼
                                          AriaApiService (Retrofit)
                                                    ▼
                                          BearerTokenInterceptor
                                                    ▼
                              Backend real (Spring Boot) — local ou Render
                                                    ▼
                                          MongoDB Atlas
```

`FakeAiRepository` (IA de apoio — insights/sugestões fora do `ai-score`) e
parte do `UserRepositoryImpl` (avatar local, cálculo de badge-unlock)
continuam locais por decisão de escopo — ver seção 14.

---

## 6. Estrutura de pastas

```
app/src/main/java/com/fiap/ariachallenge/
├── AriaApplication.kt
├── MainActivity.kt
├── data/
│   ├── local/          UserSessionStore, AuthTokenStore, AuthAccountStore,
│   │                   BadgeUnlockTracker, AvatarStorage
│   ├── mock/           MockAi (IA de apoio ainda simulada)
│   ├── remote/         AriaApiService, BearerTokenInterceptor, ApiMappers, dto/
│   ├── repository/     AuthRepositoryImpl, UserRepositoryImpl, IdeaRepositoryImpl,
│   │                   ProjectRepositoryImpl, OrientationRepositoryImpl,
│   │                   DashboardRepositoryImpl, FakeAiRepository
│   └── session/        AuthSessionManager
├── di/                 NetworkModule.kt, RepositoryModule.kt
├── domain/
│   ├── analytics/      AnalyticsMetricsCalculator
│   ├── gamification/   GamificationCalculator, GamificationPoints, ProfileMetricsCalculator
│   ├── model/          User, Idea, Project, Orientation, Badge, Notification,
│   │                   DashboardSummary, ProjectRoiSummary, StrategyRoiSummary, Ai*, ...
│   └── repository/     7 interfaces I*Repository (inclui IDashboardRepository)
├── export/             AriaAnalyticsPdfExporter.kt
├── navigation/         AriaDestinations, AriaNavGraph, *NavGraph por perfil
├── ui/
│   ├── aria/           Design system (botões, cards, chrome, pickers)
│   ├── auth/           login, register, recover
│   ├── components/     cards, gráficos, gamificação, avatar
│   ├── gestor/         telas do gestor
│   ├── lider/           telas do líder
│   ├── operador/       telas do operador
│   ├── splash/
│   ├── test/            AriaTestTags
│   └── theme/           AriaChallengeTheme, cores, tipografia
└── util/                ComposeLocalization, MoneyFormat, Extensions, ...
```

---

## 7. Navegação e telas

### Auth (comum)

| Tela | Rota | Arquivo |
|------|------|---------|
| Splash | `splash` | `ui/splash/SplashScreen.kt` |
| Login | `login` | `ui/auth/login/LoginScreen.kt` |
| Registro | `register` | `ui/auth/register/RegisterScreen.kt` |
| Recuperar senha | `recover_password` | `ui/auth/recover/RecoverPasswordScreen.kt` |

Pós-login: `navigateToRole()` em `AriaNavGraph.kt` — `popUpTo` da rota de auth.

### Operador (7 destinos)

| Tela | Rota |
|------|------|
| Home | `operador/home` |
| Minhas ideias | `operador/ideias` |
| Nova ideia | `operador/nova_ideia` |
| Detalhes ideia (com exclusão) | `operador/ideias/{ideaId}` |
| Detalhes orientação (read-only) | `operador/orientacoes/{orientationId}` |
| Notificações | `operador/notificacoes` |
| Perfil | `operador/perfil` |

### Gestor (11 destinos)

| Tela | Rota |
|------|------|
| Home | `gestor/home` |
| Pendentes | `gestor/pendentes` |
| Analisar ideia (aprovar/rejeitar/pontuar via IA) | `gestor/analisar/{ideaId}` |
| Projetos | `gestor/projetos` |
| Criar projeto | `gestor/criar_projeto?ideaId={ideaId}` |
| Detalhes projeto | `gestor/projetos/{projectId}` |
| Editar projeto | `gestor/projetos/{projectId}/editar` |
| Orientações | `gestor/orientacoes` |
| Detalhes orientação | `gestor/orientacoes/{orientationId}` |
| Notificações | `gestor/notificacoes` |
| Perfil | `gestor/perfil` |

### Líder (11 destinos)

| Tela | Rota |
|------|------|
| Dashboard | `lider/dashboard` |
| Orientações | `lider/orientacoes` |
| Criar orientação | `lider/criar_orientacao` |
| Editar orientação | `lider/orientacoes/{orientationId}/editar` |
| Detalhes orientação | `lider/orientacoes/{orientationId}` |
| Análises | `lider/analises` |
| Tendências | `lider/tendencias` |
| Projetos (somente consulta) | `lider/projetos` |
| Detalhes projeto (somente consulta, sem editar) | `lider/projetos/{projectId}` |
| Detalhes ideia | `lider/ideias/{ideaId}` |
| Notificações | `lider/notificacoes` |
| Perfil | `lider/perfil` |

> As rotas `lider/criar_projeto` e `lider/projetos/{projectId}/editar` da
> Sprint 1 foram **removidas** — o enunciado da Sprint 2 restringe
> criação/edição de projeto ao GESTOR, e o backend já aplicava isso
> (`@PreAuthorize`); o app agora reflete essa regra na navegação também.

Telas compartilhadas entre perfis: `DetalhesProjetoScreen` (parâmetro
`canEdit`), `DetalhesIdeiaScreen`, `DetalhesOrientacaoLiderScreen` (parâmetro
`readOnly`).

---

## 8. Autenticação e sessão

### Fluxo

1. Login/registro → `AriaApiService` → backend real (`POST /auth/login` ou `/auth/register`)
2. Resposta `AuthResponseDto` com `accessToken` (JWT real, assinado pelo backend) e `user`
3. `AuthSessionManager.persist()` → `AuthTokenStore` + `UserSessionStore`
4. Requests autenticados: `BearerTokenInterceptor` anexa o token; validação real no backend (Spring Security + JWT filter)
5. Token expira em 24h — login novamente após esse período

### Registro

- `POST /api/v1/auth/register` — cria usuário real no MongoDB
- `AuthRepositoryImpl` persiste sessão e retorna `User`
- `RegisterScreen` observa `registeredUserRole` e navega via `onRegistered` para o grafo do perfil
- Papel fixo: `UserRole.OPERADOR`

### Recuperação de senha

`POST /api/v1/auth/recover-password` gera uma senha temporária real no
backend (sem serviço de e-mail configurado nesta sprint — o "envio" é
simulado via log do servidor).

### Credenciais de teste

Senha padrão: **`senha123`**

| Perfil | E-mail |
|--------|--------|
| Operador | `operador@aria.com` |
| Gestor | `gestor@aria.com` |
| Líder | `lider@aria.com` |

---

## 9. Persistência (DataStore)

| DataStore | Arquivo | Conteúdo |
|-----------|---------|----------|
| `aria_user_session` | `UserSessionStore` | JSON do `User` logado (snapshot da sessão) |
| (token) | `AuthTokenStore` | JWT de acesso |
| `aria_auth_accounts` | `AuthAccountStore` | Contas registradas localmente (fallback) |
| `aria_badge_unlocks` | `BadgeUnlockTracker` | IDs de badges já exibidos por usuário (animação, não dado de negócio) |
| Avatar local | `AvatarStorage` | URI/caminho de foto de perfil (operador) — não sincroniza com backend |

Ideias, projetos, orientações e notificações **não têm mais snapshot local**
— toda leitura é uma chamada de rede ao backend real, sem cache offline.

---

## 10. API real

| Componente | Caminho |
|------------|---------|
| Base URL (dev local) | `http://10.0.3.2:8080/` (Genymotion) ou `http://10.0.2.2:8080/` (AVD padrão) |
| Base URL (produção / APK de entrega) | `https://aria-backend-p7bk.onrender.com/` |
| Interface | `data/remote/AriaApiService.kt` |
| Bearer | `BearerTokenInterceptor.kt` |
| Backend | repositório irmão `aria-backend` (Spring Boot + MongoDB Atlas) |

### Endpoints consumidos

| Recurso | Rotas |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/recover-password` |
| Ideas | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/review`, `POST /{id}/ai-score`, `DELETE /{id}` |
| Projects | `GET`, `GET /{id}` (filtrado client-side, sem endpoint dedicado), `POST`, `PUT /{id}`, `PATCH /{id}/progress`, `DELETE /{id}` |
| Orientations | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Notifications | `GET`, `PATCH /{id}/read`, `PATCH /read-all` |
| Users | `GET /me`, `GET`, `GET /{id}` |
| Dashboard | `GET /dashboard/summary`, `GET /dashboard/roi-by-strategy`, `GET /dashboard/roi-by-project` |

Especificação completa de payload/resposta de cada endpoint está na
apresentação de entrega (e no README do `aria-backend`).

---

## 11. Repositórios

| Interface | Implementação | Status |
|-----------|---------------|--------|
| `IAuthRepository` | `AuthRepositoryImpl` | Real |
| `IUserRepository` | `UserRepositoryImpl` | Real (perfil, listagem, busca, notificações); avatar e cálculo de badge-unlock continuam locais |
| `IIdeaRepository` | `IdeaRepositoryImpl` | Real |
| `IProjectRepository` | `ProjectRepositoryImpl` | Real |
| `IOrientationRepository` | `OrientationRepositoryImpl` | Real |
| `IDashboardRepository` | `DashboardRepositoryImpl` | Real |
| `IAiRepository` | `FakeAiRepository` | Simulado (decisão de escopo — ver seção 14) |

`deleteIdea` agora tem fluxo completo na UI: ícone de exclusão em
`DetalhesIdeiaScreen`, visível apenas para o autor da ideia.

---

## 12. Sistema de ROI e analytics

### Dados por entidade

| Nível | Campos |
|-------|--------|
| **Ideia** | `estimatedRoi: Double?` (não persistido na aprovação — ROI é conceito de Projeto/Dashboard, não de Ideia) |
| **Projeto** | `estimatedRoi`, `actualRoi`, `budget`, `progress` |
| **Orientação** | `roiCompact`, `roiDeltaPercent`, `ideasCount`, `projectsActive` — **calculados pelo backend** (`OrientationEnrichmentService`), não mais localmente |

### Dashboard do líder

- **Resumo (submetidas, aprovadas, taxa de conversão, ROI total, top 5 projetos):** vem de `GET /dashboard/summary` e `GET /dashboard/roi-by-project`, com fallback para cálculo local se a API falhar
- **ROI por estratégia:** seção dedicada consumindo `GET /dashboard/roi-by-strategy`, exibindo retorno de cada orientação estratégica
- **Distribuição por categoria e série mensal de ROI:** continuam calculadas no cliente (sem endpoint dedicado no backend para isso)
- **Gráficos:** `InteractiveSparkline`, `DonutChart`, `BarChart` (Canvas Compose)

### Formulários gestor/líder

ROI na UI em **milhares (K)** → multiplicado por `1000` ao salvar.

### Export PDF

`export/AriaAnalyticsPdfExporter.kt` — gerado em cache e compartilhado via `FileProvider` a partir de `AnaliseScreen`.

---

## 13. Gamificação

| Item | Detalhe |
|------|---------|
| Badges | 5 tipos: primeira ideia, 5 ideias, aprovada, high score, em projeto |
| Pontos / badges (exibição) | Calculados pelo **backend** (`GamificationService`), expostos via `GET /users/me` |
| Animação de conquista | `BadgeUnlockTracker` (local) — evita repetir a celebração, cálculo do "o que é novo" continua local |
| UI | `GamificationCards`, `BadgeDisplay`, `BadgeUnlockCelebration` |
| Perfil operador | Pontos, badges, avatar editável (`EditableProfileAvatar` + Coil) |

---

## 14. IA — real e simulada

| Funcionalidade | Status | Onde |
|---|---|---|
| **Pontuação de ideias (`ai-score`)** | **Real** — Google Gemini API, via backend | Botão "Pontuar com IA" em `AnalisarIdeiaScreen`, GESTOR |
| Sugestões/insights complementares (brief de score, insights do dashboard, chat assistente) | Simulado | `FakeAiRepository` + `MockAi.kt`, textos en/pt/es via `LocalizedMockText` |

Decisão de escopo: o diferencial de IA do enunciado (uma das 3 opções
oficiais) foi implementado de ponta a ponta via `ai-score`; os demais 8
métodos de `IAiRepository` seguem mockados — fora do escopo obrigatório da
Sprint 2, mantidos como estavam desde a Sprint 1.

---

## 15. Internacionalização (i18n)

### Locales

`res/xml/locales_config.xml`: **en**, **pt-BR**, **es**

**Idioma padrão:** inglês (`values/` sem sufixo).

### Uso no código

- UI: `stringResource(R.string.*)`
- Enums: `@StringRes` + `ComposeLocalization.kt` (`localizedName()`)
- Moeda: `MoneyFormat` / tooltips com **R$** (contexto Águia Branca)

---

## 16. Design system Aria

| Camada | Pasta | Exemplos |
|--------|-------|----------|
| Tema | `ui/theme/` | `AriaChallengeTheme`, `AriaColors`, `AriaTypography` (Outfit + IBM Plex Sans) |
| Primitivos | `ui/aria/` | `AriaCard`, `AriaButton`, `AriaChrome`, `AriaInputs`, `AriaPickers` |
| Domínio UI | `ui/components/` | `IdeaCard`, `ProjectCard`, `InteractiveSparkline`, `MetricCard`, … |

### Cores (light)

| Token | Hex |
|-------|-----|
| Primary | `#1A2540` |
| Accent | `#C87D0E` |
| Background | `#F0EDE8` |
| Success | `#34A853` |
| Error | `#C62828` |

**Dark mode:** `DarkAriaColors` + `isSystemInDarkTheme()` em `AriaChallengeTheme`.

---

## 17. ViewModels (26)

| ViewModel | Tela principal |
|-----------|----------------|
| `SplashViewModel` | Splash |
| `LoginViewModel` | Login |
| `RegisterViewModel` | Registro |
| `RecoverPasswordViewModel` | Recuperar senha |
| `HomeOperadorViewModel` | Home operador |
| `MinhasIdeiasViewModel` | Minhas ideias |
| `NovaIdeiaViewModel` | Nova ideia |
| `DetalhesIdeiaViewModel` | Detalhes ideia (inclui exclusão) |
| `NotificacoesViewModel` | Notificações |
| `PerfilOperadorViewModel` | Perfil operador |
| `BadgeCelebrationViewModel` | Celebração de badge |
| `HomeGestorViewModel` | Home gestor |
| `PendentesViewModel` | Pendentes |
| `AnalisarIdeiaViewModel` | Analisar ideia (aprovar/rejeitar/pontuar via IA) |
| `ProjetosViewModel` | Projetos |
| `CriarProjetoViewModel` | Criar projeto |
| `DetalhesProjetoViewModel` | Detalhes/editar/excluir projeto |
| `OrientacoesGestorViewModel` | Orientações gestor |
| `PerfilGestorViewModel` | Perfil gestor |
| `DashboardLiderViewModel` | Dashboard |
| `OrientacoesLiderViewModel` | Orientações líder |
| `CriarOrientacaoViewModel` | Criar/editar orientação |
| `DetalhesOrientacaoLiderViewModel` | Detalhes orientação |
| `AnaliseViewModel` | Análises |
| `TendenciasViewModel` | Tendências |
| `PerfilLiderViewModel` | Perfil líder |

---

## 18. Testes

### Unitários (`app/src/test/`)

| Arquivo | Escopo |
|---------|--------|
| `LoginViewModelTest.kt` | Validação e login |
| `RegisterViewModelTest.kt` | Validação e registro |
| `UserSessionJsonTest.kt` | Serialização de sessão |
| `GamificationCalculatorTest.kt` | Pontos e badges (cálculo local remanescente) |
| `ExampleUnitTest.kt` | Placeholder |

### Instrumentados (`app/src/androidTest/`)

| Arquivo | Escopo |
|---------|--------|
| `LoginScreenInstrumentedTest.kt` | UI de login + tags |
| `ExampleInstrumentedTest.kt` | Placeholder |

```bash
gradlew test
gradlew connectedAndroidTest
```

---

## 19. Como executar

### Pré-requisitos

- Android Studio (Ladybug ou superior)
- JDK 11+
- SDK Android 36
- Emulador (Genymotion recomendado) ou dispositivo físico API 26+
- Backend rodando — local (`aria-backend`, ver README do backend) **ou**
  apontando para o backend hospedado no Render (já configurado no
  `NetworkModule.kt` na hora de gerar o APK de entrega)

### Build e run

```bash
# Windows
gradlew.bat assembleDebug
gradlew.bat installDebug

# Testes
gradlew.bat test
```

No Android Studio: Sync Gradle → módulo `app` → Run.

### Trocar o backend de destino

Em `di/NetworkModule.kt`, ajuste `BASE_URL`:
- Desenvolvimento local via Genymotion: `http://10.0.3.2:8080/`
- Desenvolvimento local via AVD padrão: `http://10.0.2.2:8080/`
- Produção (APK de entrega): `https://aria-backend-p7bk.onrender.com/`

> ⚠️ **Atenção:** o plano gratuito do Render "dorme" após um período de
> inatividade. A primeira requisição após esse intervalo pode levar até
> ~1 minuto para responder — isso é esperado, não é um bug do app nem do
> backend.

---

## 20. Checklist de implementação

| Funcionalidade | Status | Observação |
|----------------|--------|------------|
| Login / Logout | ✅ | JWT real + sessão DataStore |
| Registro | ✅ | Backend real, sessão, auto-navegação; papel OPERADOR |
| Recuperar senha | ✅ | Backend real, senha temporária gerada de verdade |
| CRUD Ideias | ✅ | Backend real, **inclui exclusão pela UI** |
| Analisar ideias (gestor) | ✅ | Aprovar/rejeitar, score, feedback, **pontuação por IA real** |
| CRUD Projetos | ✅ | Create, update, delete, progresso, marcos, equipe — tudo real |
| CRUD Orientações | ✅ | Líder CRUD; gestor/operador leitura — backend real |
| Dashboard líder | ✅ | Consumindo endpoints reais de relatório |
| Gráficos / Analytics | ✅ | Canvas + PDF export |
| Gamificação | ✅ | Pontos/badges reais (backend), animação local |
| Notificações | ✅ | Backend real, leitura e marcação persistidas |
| i18n UI (EN/PT/ES) | ✅ | Strings por locale |
| Persistência sessão + avatar | ✅ | DataStore |
| API real | ✅ | Sprint 2 |
| Backend hospedado (Render + Atlas) | ✅ | Não depende de ambiente local para rodar o APK de entrega |
| Testes automatizados | ⚠️ | Cobertura básica (auth, gamificação, sessão) — não expandida na Sprint 2 |

---

## 21. Limitações conhecidas

1. **Avatar de perfil** não sincroniza com o backend — permanece local ao dispositivo.
2. **Membros de equipe de projeto**: a lista vem real de `GET /users`, mas ainda não há tela de gestão de usuários além de perfil/listagem.
3. **IA complementar** (insights de dashboard, chat assistente, brief detalhado) continua simulada — só a pontuação de ideias (`ai-score`) é real, por decisão de escopo.
4. **Registro** sempre cria operador — sem escolha de perfil.
5. **Mensagens de erro de rede** em algumas listas ainda aparecem como "vazio" em vez de uma mensagem específica de falha (polimento de UX pendente).
6. **Acessibilidade** parcial — alguns ícones decorativos sem `contentDescription`.

---

## 22. Referência rápida — rotas

<details>
<summary>Lista completa de rotas</summary>

**Auth:** `splash`, `login`, `register`, `recover_password`

**Operador:** `operador/home`, `operador/ideias`, `operador/nova_ideia`, `operador/ideias/{ideaId}`, `operador/orientacoes/{orientationId}`, `operador/notificacoes`, `operador/perfil`

**Gestor:** `gestor/home`, `gestor/pendentes`, `gestor/analisar/{ideaId}`, `gestor/projetos`, `gestor/criar_projeto`, `gestor/projetos/{projectId}`, `gestor/projetos/{projectId}/editar`, `gestor/orientacoes`, `gestor/orientacoes/{orientationId}`, `gestor/notificacoes`, `gestor/perfil`

**Líder:** `lider/dashboard`, `lider/orientacoes`, `lider/criar_orientacao`, `lider/orientacoes/{orientationId}`, `lider/orientacoes/{orientationId}/editar`, `lider/analises`, `lider/tendencias`, `lider/projetos`, `lider/projetos/{projectId}`, `lider/ideias/{ideaId}`, `lider/notificacoes`, `lider/perfil`

</details>

---

*Documentação gerada a partir do código-fonte ao final da Sprint 2.*