# ARIA — Sistema de Gestão de Inovação

Documentação técnica do aplicativo Android, alinhada ao **Kickoff Challenge Águia Branca — Sprint 1** (`estudo/Kickoff_Challenge_AguiaBranca_Sprint1.pdf`). Descreve o que está implementado no repositório em **maio/2026**, sem suposições de backend de produção.

---

## 1. Visão geral

| Item | Detalhe |
|------|---------|
| **Nome** | ARIA (Innovation Management System) |
| **Contexto** | FIAP Challenge em parceria com **Grupo Águia Branca** |
| **Objetivo** | Integrar estratégia, pessoas e execução no ciclo de inovação: ideias → análise → projetos → orientações → mensuração (ROI) |
| **Package** | `com.fiap.ariachallenge` |
| **Application ID** | `com.fiap.ariachallenge` |
| **Sprint atual** | **Sprint 1** — app nativo Android com backend simulado (REST mock) |
| **Sprint 2 (futuro)** | Backend real (Java/C#), microsserviços, persistência em nuvem |

### Estado do projeto

**MVP acadêmico funcional** com:

- UI completa em Jetpack Compose para **Operador**, **Gestor** e **Líder**
- API REST **mockada** com Retrofit + interceptor local + JWT simulado
- Persistência local via **DataStore** (sessão, token, contas registradas, snapshot de negócio, badges)
- Sem Firebase, Supabase, Room ou API de produção
- Extras: gamificação, IA simulada trilíngue, gráficos Canvas, export PDF

---

## 2. Alinhamento ao kickoff (Sprint 1)

| Requisito (PDF) | Status | Implementação |
|-----------------|--------|-----------------|
| App nativo Android | ✅ | Kotlin + Compose |
| Operador: consultar orientações | ✅ | Home + detalhe read-only (`operador/orientacoes/{id}`) |
| Operador: cadastrar ideias | ✅ | `NovaIdeiaScreen` |
| Operador: acompanhar status | ✅ | `MinhasIdeiasScreen`, `DetalhesIdeiaScreen`, notificações |
| Gestor: consultar orientações | ✅ | Lista + detalhe (somente leitura) |
| Gestor: priorizar e aprovar ideias | ✅ | Fila com ordenação por score, `AnalisarIdeiaScreen` |
| Gestor: CRUD projetos + atualizar resultados | ✅ | Criar, editar, excluir, ROI, progresso, marcos |
| Líder: CRUD orientações | ✅ | Criar, editar, excluir |
| Líder: consultar projetos | ✅ | Lista, detalhes (etapa, prazo, ROI, marcos) |
| Líder: dashboard (ROI, resultados) | ✅ | Métricas, funil, gráficos, insights IA |
| Login 3 perfis + sessão | ✅ | JWT mock + DataStore |
| Conectividade externa efetiva | ✅ | REST mock funcional (alternativa válida ao Firebase no regulamento) |
| Inovação aberta (ecossistema) | ❌ | Fora do escopo explícito da S1 |
| Backend real | ❌ | Previsto para Sprint 2 |

---

## 3. Perfis de usuário

| Perfil | `UserRole` | Responsabilidades |
|--------|------------|-------------------|
| **Operador** | `OPERADOR` | Home, orientações (leitura), minhas ideias, nova ideia, detalhes, notificações, perfil, gamificação |
| **Gestor** | `GESTOR` | Home, pendentes, analisar ideia, projetos (CRUD), orientações (leitura), notificações, perfil |
| **Líder** | `LIDER` | Dashboard, orientações (CRUD), análises, tendências, projetos, notificações, perfil, export PDF |

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

Room, Firebase, Supabase, backend Java/C# (Sprint 2).

---

## 5. Arquitetura

### Padrão

**MVVM** + **Clean-ish layering**:

```
ui/           → Compose screens, design system Aria, navegação por perfil
domain/       → modelos, analytics, gamificação, interfaces de repositório
data/         → Fake*Repository, mock, remote (API, interceptor, DTOs)
di/           → Hilt (RepositoryModule, NetworkModule)
util/         → localização Compose, formatação, parsers
export/       → AriaAnalyticsPdfExporter
```

ViewModels chamam repositórios diretamente (sem camada `usecase/`).

### Injeção de dependência

- `@HiltAndroidApp` — `AriaApplication`
- `@AndroidEntryPoint` — `MainActivity`
- `@HiltViewModel` — ViewModels
- `RepositoryModule` — binds `Fake*` → `I*Repository`
- `NetworkModule` — OkHttp, Retrofit, `AriaApiService`

### State management

- `MutableStateFlow` / `StateFlow` nos ViewModels
- UI: `collectAsStateWithLifecycle()`
- Listas: `Flow` via `InMemoryApiStore` (`ideas`, `projects`, `orientations`)
- Navegação one-shot: lambdas `onNavigate` / `LaunchedEffect`

### Diagrama simplificado

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐
│   Screen    │────▶│  ViewModel   │────▶│  I*Repository   │
└─────────────┘     └──────────────┘     └────────┬────────┘
                                                    │
                    ┌───────────────────────────────┴───────────────────────────────┐
                    ▼                               ▼                               ▼
            FakeAuthRepository              FakeIdeaRepository              FakeProjectRepository
                    │                               │                               │
                    └───────────────────────────────┼───────────────────────────────┘
                                                    ▼
                                          AriaApiService (Retrofit)
                                                    ▼
                              BearerTokenInterceptor + AriaMockApiInterceptor
                                                    ▼
                                          InMemoryApiStore (+ DataStore snapshot)
```

---

## 6. Estrutura de pastas

```
app/src/main/java/com/fiap/ariachallenge/
├── AriaApplication.kt
├── MainActivity.kt
├── data/
│   ├── local/          UserSessionStore, AuthTokenStore, AuthAccountStore,
│   │                   InMemoryApiDataStore, BadgeUnlockTracker, AvatarStorage
│   ├── mock/           MockUsers, MockIdeas, MockProjects, MockOrientations,
│   │                   MockNotifications, MockAi, LocalizedMockText
│   ├── remote/         AriaApiService, AriaMockApiInterceptor, BearerTokenInterceptor,
│   │                   InMemoryApiStore, AuthAccountRegistry, ApiMappers, dto/
│   ├── repository/     FakeAuth, FakeUser, FakeIdea, FakeProject, FakeOrientation, FakeAi
│   ├── security/       MockJwtProvider
│   └── session/        AuthSessionManager
├── di/                 NetworkModule.kt, RepositoryModule.kt
├── domain/
│   ├── analytics/      AnalyticsMetricsCalculator
│   ├── gamification/   GamificationCalculator, GamificationPoints, ProfileMetricsCalculator
│   ├── model/          User, Idea, Project, Orientation, Badge, Notification, Ai*, ...
│   └── repository/     6 interfaces I*Repository
├── export/             AriaAnalyticsPdfExporter.kt
├── navigation/         AriaDestinations, AriaNavGraph, *NavGraph por perfil
├── ui/
│   ├── aria/           Design system (botões, cards, chrome, pickers)
│   ├── auth/           login, register, recover
│   ├── components/     cards, gráficos, gamificação, avatar
│   ├── gestor/         telas do gestor
│   ├── lider/          telas do líder
│   ├── operador/       telas do operador
│   ├── splash/
│   ├── test/           AriaTestTags
│   └── theme/          AriaChallengeTheme, cores, tipografia
└── util/               ComposeLocalization, MoneyFormat, Extensions, ...
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
| Detalhes ideia | `operador/ideias/{ideaId}` |
| Detalhes orientação (read-only) | `operador/orientacoes/{orientationId}` |
| Notificações | `operador/notificacoes` |
| Perfil | `operador/perfil` |

### Gestor (11 destinos)

| Tela | Rota |
|------|------|
| Home | `gestor/home` |
| Pendentes | `gestor/pendentes` |
| Analisar ideia | `gestor/analisar/{ideaId}` |
| Projetos | `gestor/projetos` |
| Criar projeto | `gestor/criar_projeto?ideaId={ideaId}` |
| Detalhes projeto | `gestor/projetos/{projectId}` |
| Editar projeto | `gestor/projetos/{projectId}/editar` |
| Orientações | `gestor/orientacoes` |
| Detalhes orientação | `gestor/orientacoes/{orientationId}` |
| Notificações | `gestor/notificacoes` |
| Perfil | `gestor/perfil` |

### Líder (13 destinos)

| Tela | Rota |
|------|------|
| Dashboard | `lider/dashboard` |
| Orientações | `lider/orientacoes` |
| Criar orientação | `lider/criar_orientacao` |
| Editar orientação | `lider/orientacoes/{orientationId}/editar` |
| Detalhes orientação | `lider/orientacoes/{orientationId}` |
| Análises | `lider/analises` |
| Tendências | `lider/tendencias` |
| Projetos | `lider/projetos` |
| Detalhes / criar / editar projeto | rotas `lider/projetos/...`, `lider/criar_projeto` |
| Detalhes ideia | `lider/ideias/{ideaId}` |
| Notificações | `lider/notificacoes` |
| Perfil | `lider/perfil` |

Telas compartilhadas entre perfis: `DetalhesProjetoScreen`, `DetalhesIdeiaScreen`, `DetalhesOrientacaoLiderScreen` (modo `readOnly` para operador/gestor).

---

## 8. Autenticação e sessão

### Fluxo

1. Login/registro → `AriaApiService` → `AriaMockApiInterceptor`
2. Resposta `AuthResponseDto` com `accessToken` (JWT mock) e `user`
3. `AuthSessionManager.persist()` → `AuthTokenStore` + `UserSessionStore`
4. Requests autenticados: `BearerTokenInterceptor` + validação no mock (`MockJwtProvider`)

### Registro

- `POST /api/v1/auth/register` — cria usuário em `AuthAccountRegistry`
- `FakeAuthRepository` persiste sessão e retorna `User`
- `RegisterScreen` observa `registeredUserRole` e navega via `onRegistered` para o grafo do perfil
- Papel fixo: `UserRole.OPERADOR`

### Erros (códigos)

| Código | Uso |
|--------|-----|
| `ERR_INVALID_CREDENTIALS` | Login inválido |
| `ERR_INVALID_EMAIL` | Recuperar senha |
| `ERR_EMAIL_EXISTS` | E-mail já cadastrado |
| `ERR_INVALID` | Payload inválido no registro |
| `ERR_NAME`, `ERR_EMAIL`, … | Validação local no `RegisterViewModel` |

Telas mapeiam códigos para `stringResource(R.string.*)`.

### Credenciais demo

Senha padrão: **`aria123`**

| Perfil | E-mail | Usuário mock |
|--------|--------|--------------|
| Operador | `operador@aria.com` | `MockUsers.operador1` |
| Gestor | `gestor@aria.com` | `MockUsers.gestor1` |
| Líder | `lider@aria.com` | `MockUsers.lider1` |

Alternativos corporativos: `rafael.costa@aguiabranca.com`, `carlos.mendes@aguiabranca.com`, `carlos.mendes.diretor@aguiabranca.com` (mesma senha).

---

## 9. Persistência (DataStore)

| DataStore | Arquivo | Conteúdo |
|-----------|---------|----------|
| `aria_user_session` | `UserSessionStore` | JSON do `User` logado |
| (token) | `AuthTokenStore` | JWT de acesso |
| `aria_auth_accounts` | `AuthAccountStore` | Contas registradas (e-mail, senha, user) |
| `aria_api_snapshot` | `InMemoryApiDataStore` | Snapshot de ideias, projetos, orientações |
| `aria_badge_unlocks` | `BadgeUnlockTracker` | IDs de badges já exibidos por usuário |
| Avatar local | `AvatarStorage` | URI/caminho de foto de perfil (operador) |

### Comportamento ao reiniciar o app

| Dado | Persiste? |
|------|-----------|
| Sessão + token | ✅ |
| Contas registradas | ✅ (`AuthAccountRegistry` + `AuthAccountStore`) |
| Ideias / projetos / orientações (CRUD) | ✅ (`InMemoryApiStore.persistSnapshot()` após mutações no interceptor) |
| Notificações (lidas/não lidas) | ❌ RAM (`MockNotifications`) |
| Seed inicial | Mock seed se snapshot vazio na primeira execução |

---

## 10. API mock (REST)

| Componente | Caminho |
|------------|---------|
| Base URL | `https://aria-mock.api/` |
| Interface | `data/remote/AriaApiService.kt` |
| Interceptor | `AriaMockApiInterceptor.kt` |
| Store | `InMemoryApiStore.kt` |
| Bearer | `BearerTokenInterceptor.kt` |
| Rotas públicas | `AuthConfig` — login e register |

### Endpoints implementados

| Método | Path | Ação |
|--------|------|------|
| POST | `/api/v1/auth/login` | Login |
| POST | `/api/v1/auth/register` | Registro |
| GET/POST/PUT/DELETE | `/api/v1/ideas` | CRUD ideias |
| GET/POST/PUT/DELETE | `/api/v1/projects` | CRUD projetos |
| GET/POST/PUT/DELETE | `/api/v1/orientations` | CRUD orientações |

Mutações que alteram store disparam `persistSnapshot()` quando `shouldPersist()` retorna true.

Repositórios `Fake*` usam `delay()` para simular latência de rede.

---

## 11. Repositórios

| Interface | Implementação | Funções principais |
|-----------|---------------|-------------------|
| `IAuthRepository` | `FakeAuthRepository` | login, register, logout, getCurrentUser, recoverPassword |
| `IUserRepository` | `FakeUserRepository` | usuário atual, notificações, usuários para equipe de projeto |
| `IIdeaRepository` | `FakeIdeaRepository` | CRUD + queries por autor/status/pendentes |
| `IProjectRepository` | `FakeProjectRepository` | listagem, create, update, **delete** |
| `IOrientationRepository` | `FakeOrientationRepository` | CRUD orientações |
| `IAiRepository` | `FakeAiRepository` | insights, score, timeline, bundle de análises |

`deleteIdea` existe na API/repositório, mas **não há fluxo na UI** para o operador/gestor excluir ideias.

---

## 12. Sistema de ROI e analytics

### Dados por entidade

| Nível | Campos |
|-------|--------|
| **Ideia** | `estimatedRoi: Double?` (na análise) |
| **Projeto** | `estimatedRoi`, `actualRoi`, `budget`, `progress` |
| **Orientação** | `roiCompact`, `roiDeltaPercent` (enriquecidos por `OrientationEnricher`) |

### Dashboard do líder

- **ROI acumulado:** soma `(actualRoi ?: estimatedRoi)` de todos os projetos
- **Série mensal:** `AnalyticsMetricsCalculator.monthlyRoiSeries()` — soma ROI dos projetos com `startDate` no mês
- **Deltas:** calculados mês a mês (`monthMetricDeltas`), não hardcoded
- **Funil:** submetidas → aprovadas → em projeto → concluídas
- **Gráficos:** `InteractiveSparkline`, `DonutChart`, `BarChart` (Canvas Compose)

### Formulários gestor/líder

ROI na UI em **milhares (K)** → multiplicado por `1000` ao salvar.

### Export PDF

`export/AriaAnalyticsPdfExporter.kt` — gerado em cache e compartilhado via `FileProvider` a partir de `AnaliseScreen`.

---

## 13. Gamificação

| Item | Detalhe |
|------|---------|
| Badges | 5 (`Badge` enum): primeira ideia, 5 ideias, aprovada, high score, em projeto |
| Pontos | `GamificationCalculator` + `GamificationPoints` |
| UI | `GamificationCards`, `BadgeDisplay`, `BadgeUnlockCelebration` |
| Persistência | `BadgeUnlockTracker` — evita repetir animação de conquista |
| Perfil operador | Pontos, badges, avatar editável (`EditableProfileAvatar` + Coil) |

---

## 14. IA simulada

- `FakeAiRepository` + `MockAi.kt`
- Textos **en / pt / es** via `LocalizedMockText`
- Por perfil: sugestões (operador), score/brief (gestor), insights e previsões (líder)
- Não há modelo de ML real — regras e textos mockados

---

## 15. Internacionalização (i18n)

### Locales

`res/xml/locales_config.xml`: **en**, **pt-BR**, **es**

| Arquivo | Chaves |
|---------|--------|
| `values/strings.xml` | 636 |
| `values/strings_i18n_completion.xml` | 199 |
| **Total EN** | **835** |
| **Total PT-BR** | **835** |
| **Total ES** | **835** |

**Idioma padrão:** inglês (`values/` sem sufixo).

### Uso no código

- UI: `stringResource(R.string.*)`
- Enums: `@StringRes` + `ComposeLocalization.kt` (`localizedName()`)
- Mocks de conteúdo (`MockIdeas`, notificações): **português fixo** nos títulos/descrições principais
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

## 17. Dados mock (seed)

| Arquivo | Quantidade |
|---------|------------|
| `MockUsers.kt` | 7 usuários (4 operadores, 2 gestores, 1 líder) |
| `MockIdeas.kt` | 10 ideias |
| `MockProjects.kt` | 5 projetos |
| `MockOrientations.kt` | 5 orientações |
| `MockNotifications.kt` | 7 notificações |
| `MockAi.kt` | Insights/sugestões localizados |

---

## 18. ViewModels (26)

| ViewModel | Tela principal |
|-----------|----------------|
| `SplashViewModel` | Splash |
| `LoginViewModel` | Login |
| `RegisterViewModel` | Registro |
| `RecoverPasswordViewModel` | Recuperar senha |
| `HomeOperadorViewModel` | Home operador |
| `MinhasIdeiasViewModel` | Minhas ideias |
| `NovaIdeiaViewModel` | Nova ideia |
| `DetalhesIdeiaViewModel` | Detalhes ideia |
| `NotificacoesViewModel` | Notificações |
| `PerfilOperadorViewModel` | Perfil operador |
| `BadgeCelebrationViewModel` | Celebração de badge |
| `HomeGestorViewModel` | Home gestor |
| `PendentesViewModel` | Pendentes |
| `AnalisarIdeiaViewModel` | Analisar ideia |
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

## 19. Testes

### Unitários (`app/src/test/`)

| Arquivo | Escopo |
|---------|--------|
| `LoginViewModelTest.kt` | Validação e login |
| `RegisterViewModelTest.kt` | Validação e registro |
| `UserSessionJsonTest.kt` | Serialização de sessão |
| `GamificationCalculatorTest.kt` | Pontos e badges |
| `ExampleUnitTest.kt` | Placeholder |

### Instrumentados (`app/src/androidTest/`)

| Arquivo | Escopo |
|---------|--------|
| `LoginScreenInstrumentedTest.kt` | UI de login + tags |
| `ExampleInstrumentedTest.kt` | Placeholder |

### Test tags (`AriaTestTags.kt`)

`LoginEmail`, `LoginPassword`, `LoginSubmit`, `RegisterName`, `RegisterEmail`, `RegisterSubmit`, `NovaIdeiaTitle`, `NovaIdeiaSubmit`, `DashboardScreen`

```bash
gradlew test
gradlew connectedAndroidTest
```

---

## 20. Dependências (`app/build.gradle.kts`)

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)
    // ... ver libs.versions.toml
}
```

---

## 21. Como executar

### Pré-requisitos

- Android Studio (Ladybug ou superior)
- JDK 11+
- SDK Android 36
- Emulador ou dispositivo API 26+

### Build e run

```bash
# Windows
gradlew.bat assembleDebug
gradlew.bat installDebug

# Testes
gradlew.bat test
```

No Android Studio: Sync Gradle → módulo `app` → Run.

---

## 22. Checklist de implementação

| Funcionalidade | Status | Observação |
|----------------|--------|------------|
| Login / Logout | ✅ | JWT mock + sessão DataStore |
| Registro | ✅ | API mock, sessão, auto-navegação; papel OPERADOR |
| Recuperar senha | ✅ | Mock com `ERR_INVALID_EMAIL` |
| CRUD Ideias | ✅ | API; UI sem delete de ideia |
| Analisar ideias (gestor) | ✅ | Aprovar/rejeitar, score, feedback, ROI estimado |
| CRUD Projetos | ✅ | Create, update, **delete** |
| CRUD Orientações | ✅ | Líder CRUD; gestor/operador leitura |
| Dashboard líder | ✅ | Métricas reais, funil, ROI, gráficos |
| Gráficos / Analytics | ✅ | Canvas + PDF export |
| Gamificação | ✅ | Badges, pontos, celebração |
| Notificações | ⚠️ | Lista mock; estado lido só em RAM |
| i18n UI (EN/PT/ES) | ✅ | 835 strings por locale |
| i18n conteúdo mock | ⚠️ | Ideias/notificações em PT fixo |
| Persistência sessão + contas + negócio | ✅ | DataStore |
| Persistência notificações | ❌ | RAM |
| API real / Firebase | ❌ | Sprint 2 |
| Testes automatizados | ⚠️ | Cobertura básica (auth, gamificação, sessão) |

---

## 23. Limitações conhecidas

1. **Backend simulado** — sem rede externa real; adequado à Sprint 1.
2. **Notificações** não persistem entre sessões.
3. **Conteúdo mock** (títulos de ideias, etc.) em português independente do locale do sistema.
4. **Registro** sempre cria operador — sem escolha de perfil.
5. **Exclusão de ideias** só na camada API, sem tela.
6. **Inovação aberta** (ecossistema externo) não modelada no app.
7. **Acessibilidade** parcial — alguns ícones decorativos sem `contentDescription`.

---

## 24. Sprint 2 (roadmap)

Conforme kickoff e `arquitetura_aguiabranca.html` (referência da equipe):

- Backend Java/C# com APIs reais
- Autenticação e autorização por nível no servidor
- Substituir `Fake*Repository` por implementações HTTP
- Persistência em PostgreSQL / MongoDB por microsserviço
- IA com serviço dedicado (opcional)
- Mesmas telas Compose — troca na camada `data/`

---

## 25. Métricas do código

| Métrica | Valor (aprox.) |
|---------|----------------|
| Arquivos `.kt` em `main` | 180 |
| Linhas Kotlin em `main` | ~20.660 |
| `@Composable` | ~230 |
| ViewModels | 26 |
| Screens (`*Screen.kt`) | 27 |
| Componentes `ui/components/` | 27 |
| Primitivos `ui/aria/` | 9 |
| Strings por locale | 835 |

---

## 26. Referência rápida — rotas

<details>
<summary>Lista completa de rotas</summary>

**Auth:** `splash`, `login`, `register`, `recover_password`

**Operador:** `operador/home`, `operador/ideias`, `operador/nova_ideia`, `operador/ideias/{ideaId}`, `operador/orientacoes/{orientationId}`, `operador/notificacoes`, `operador/perfil`

**Gestor:** `gestor/home`, `gestor/pendentes`, `gestor/analisar/{ideaId}`, `gestor/projetos`, `gestor/criar_projeto`, `gestor/projetos/{projectId}`, `gestor/projetos/{projectId}/editar`, `gestor/orientacoes`, `gestor/orientacoes/{orientationId}`, `gestor/notificacoes`, `gestor/perfil`

**Líder:** `lider/dashboard`, `lider/orientacoes`, `lider/criar_orientacao`, `lider/orientacoes/{orientationId}`, `lider/orientacoes/{orientationId}/editar`, `lider/analises`, `lider/tendencias`, `lider/projetos`, `lider/projetos/{projectId}`, `lider/criar_projeto`, `lider/projetos/{projectId}/editar`, `lider/ideias/{ideaId}`, `lider/notificacoes`, `lider/perfil`

</details>

---

*Documentação gerada a partir do código-fonte. Para requisitos de negócio completos, consulte `estudo/Kickoff_Challenge_AguiaBranca_Sprint1.pdf`.*
