# PetCare Connect - Aplicativo Mobile Nativo (Android / Java)

**Universidade Paulista - UNIP**  
**Projeto Integrado Multidisciplinar (PIM IV)**  
**Curso Superior de Tecnologia em Análise e Desenvolvimento de Sistemas**

---

## 1. Visão Geral

O **PetCare Connect Mobile** é a aplicação nativa para dispositivos móveis desenvolvida em **Java** (Android SDK) para atender aos requisitos da **Etapa 5 (Desenvolvimento Mobile)** e da **Etapa 6 (Arquitetura de Software)** do manual do PIM IV.

O aplicativo oferece uma experiência completa e moderna para tutores e profissionais veterinários, integrada à **REST API em ASP.NET Core** (desenvolvida no PIM III) com suporte a operação online e modo de contingência offline com persistência local.

---

## 2. Requisitos Atendidos do PIM IV (Etapa 5)

| Requisito do Manual | Implementação no Projeto | Componente / Classe |
| :--- | :--- | :--- |
| **Telas Principais** | Login, Cadastro, Dashboard, Pets, Consultas, Vacinas, Relatórios, Avisos | `com.unip.petcareconnect.ui.*` |
| **Fluxo de Navegação** | Dashboard com Cards informativos, cabeçalhos com Up Navigation, Dialogs modais para formulários | `MainActivity.java`, CoordinatorLayout, Toolbars |
| **Autenticação** | Login com JWT (Bearer Token), persistência de sessão e perfil do usuário | `SessionManager.java`, `LoginActivity.java` |
| **Integração com a API** | Consumo dos endpoints REST com cliente HTTP assíncrono e interceptor de Token | `ApiClient.java`, `ApiService.java` (Retrofit 2 + OkHttp) |
| **Sincronização de Dados** | Repositório híbrido: busca na API e armazena cache local persistente; executa offline se a API estiver indisponível | `PetCareRepository.java` (Cache SharedPreferences + JSON) |
| **Regras de Negócio** | RN-001 a RN-006 (validação de choque de horários, cancelamento tardio < 24h, raça do pet RF-002) | `PetCareRepository.java`, `ConsultasActivity.java` |

---

## 3. Arquitetura de Software (Etapa 6)

O projeto adota o padrão arquitetural em camadas **Repository Pattern** e **MVC/MVVM** recomendado pelo Google para aplicações Android corporativas:

```
app/src/main/java/com/unip/petcareconnect/
├── api/                  # Camada de Rede e Serviços REST
│   ├── ApiClient.java        # Configuração do Retrofit e OkHttpClient com Bearer Token Interceptor
│   └── ApiService.java       # Definição das rotas REST (/api/Auth, /api/Pets, /api/Consultas, etc.)
├── model/                # Modelos de Domínio e DTOs
│   ├── Pet.java              # Entidade Pet (inclui raça RF-002, espécie, idade, peso)
│   ├── Consulta.java         # Agendamento e status da consulta
│   ├── Vacina.java           # Carteira de vacinação com datas e lotes
│   ├── Relatorio.java        # Prontuário e laudo clínico veterinário
│   ├── Aviso.java            # Alertas e lembretes com prioridades
│   ├── Usuario.java          # Usuário do sistema
│   └── LoginRequest.java / LoginResponse.java / CadastroRequest.java
├── repository/           # Camada de Acesso a Dados Híbrida
│   └── PetCareRepository.java # Gerencia chamadas assíncronas à API e sincronismo offline com seeds
├── util/                 # Utilitários e Infraestrutura
│   └── SessionManager.java   # Gerenciador de credenciais, sessão JWT e URL do servidor
├── adapter/              # Adaptadores para Listagens Dinâmicas (RecyclerView)
│   ├── PetAdapter.java
│   ├── ConsultaAdapter.java
│   ├── VacinaAdapter.java
│   ├── RelatorioAdapter.java
│   └── AvisoAdapter.java
└── ui/                   # Camada de Apresentação (Activities & Controladores)
    ├── LoginActivity.java
    ├── CadastroActivity.java
    ├── MainActivity.java
    ├── PetsActivity.java
    ├── ConsultasActivity.java
    ├── VacinasActivity.java
    ├── RelatoriosActivity.java
    └── AvisosActivity.java
```

----

## 4. Credenciais de Teste e Modo Offline

O aplicativo possui dados de demonstração integrados em seu repositório local. Ele funciona **imediatamente mesmo sem a API estar rodando**, permitindo a apresentação completa das telas para a banca examinadora da UNIP:

| Perfil | Usuário / E-mail | Senha |
| :--- | :--- | :--- |
| **Tutor (Padrão)** | `tutor_teste` | `Tutor123!` |
| **Veterinário** | `vet_teste` | `Vet123!` |
| **Administrador** | `admin_teste` | `Admin123!` |

> **Nota:** Você também pode clicar em **"Cadastre-se"** na tela de login para registrar um novo tutor com validação de dados em tempo real.

---

## 5. Conexão com o Backend ASP.NET Core (PIM III)

Quando a API web do PetCare Connect estiver em execução na sua máquina local:
- No emulador padrão do Android Studio, o endereço padrão `http://10.0.2.2:5000/` já vem pré-configurado e aponta para o `localhost` do seu computador.
- Caso utilize um celular físico via Wi-Fi, segure o clique no subtítulo da tela de Login para abrir o diálogo de configuração e digite o IP local da sua máquina (ex: `http://192.168.1.100:5000/`).

---

## 6. Tecnologias e Bibliotecas Utilizadas

- **Linguagem**: Java 17 (Nativo Android)
- **UI & Design**: Google Material Components 1.11.0, CoordinatorLayout, ConstraintLayout, SwipeRefreshLayout
- **Rede & Comunicação**: Square Retrofit 2.9.0 + OkHttp 4.12.0 com Interceptor de Autenticação JWT
- **Serialização de Dados**: Google Gson 2.10.1
- **Armazenamento Local**: Android SharedPreferences com criptografia e serialização JSON estruturada
