# Encurtador de URLs 🚀

Este projeto é uma API REST corporativa desenvolvida em **Spring Boot** para encurtamento de URLs. O sistema recebe uma URL original longa, gera uma chave aleatória única (Short Key) e realiza o redirecionamento automático (HTTP 302) quando o link encurtado é acessado.

Para garantir alta performance e escalabilidade sob alto volume de acessos, a arquitetura utiliza o **Redis** como camada de cache na memória, evitando consultas repetidas ao banco de dados relacional **H2**.

## 🛠️ Tecnologias Utilizadas

* **Java 21** & **Spring Boot 3.x / 4.x**
* **Spring Data JPA** & **Banco de Dados H2** (Em memória)
* **Spring Data Redis** (Camada de Cache com TTL de 1 dia)
* **Docker & Docker Compose** (Orquestração do ambiente Redis)
* **Springdoc OpenAPI 3 (Swagger)** (Documentação interativa da API)
* **Lombok** (Produtividade e redução de código boilerplate)

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para clonar, configurar e rodar o projeto localmente na sua máquina.

### 1. Pré-requisitos
Certifique-se de ter instalado em sua máquina:
* **Java 21** ou superior.
* **Maven 3.x**.
* **Docker** e **Docker Compose**.

### 2. Subindo a Infraestrutura com Docker
O projeto utiliza o Docker para rodar o servidor Redis e a ferramenta de monitoramento Redis Insight.

Abra o terminal na raiz do projeto (onde está o arquivo `docker-compose.yml`) e execute o comando abaixo para iniciar os serviços em segundo plano:

```bash
docker compose up -d
```

> **Nota:** Para verificar se os containers subiram com sucesso, você pode rodar `docker ps`.

### 3. Executando a Aplicação Spring Boot
Com a infraestrutura do Redis activa, você pode iniciar o servidor do Spring Boot utilizando o Maven. Execute o seguinte comando no terminal:

```bash
mvn spring-boot:run
```

O servidor será iniciado na porta padrão **`8080`**.

> ### ⚠️ Aviso Importante sobre as URLs
> Para que o encurtador funcione corretamente, **é obrigatório incluir o protocolo (`http://` ou `https://`)** antes do endereço (ex: `https://www.google.com`). Se você digitar apenas `www.google.com`, a validação do sistema recusará a requisição por falta de formato de URL válido.

---

## 🔍 Ferramentas de Monitoramento e Teste

Após iniciar o projeto, você poderá acessar as seguintes interfaces pelo seu navegador:

* **Documentação Swagger (OpenAPI 3):**  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
  *Utilize para testar os endpoints de encurtamento diretamente pela interface visual bem documentada.*

* **Console do Banco de Dados H2:**  
  [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  * **JDBC URL:** `jdbc:h2:mem:url_db`
  * **User Name:** `sa`
  * **Password:** *(deixe em branco)*

* **Redis Insight (Interface Visual do Cache):**  
  [http://localhost:5540](http://localhost:5540)  
  *Permite visualizar graficamente as chaves cacheadas (`shortKey`) que o Spring Boot grava temporariamente na memória do Redis.*

---

## 📌 Endpoints da API

**Endereço Base da API:** `http://localhost:8080`

### 1. Encurtar uma URL
* **Método/Rota:** `POST http://localhost:8080/api/v1/url/shorten`
* **Descrição:** Recebe a URL longa. Lembre-se de enviar sempre com o protocolo incluído.
* **Corpo da Requisição (JSON):**
```json
{
  "url": "https://www.google.com"
}
```
* **Resposta de Sucesso (200 OK):**
```json
{
  "shortUrl": "http://localhost:8080/api/v1/url/aB3xD9"
}
```

### 2. Redirecionar para URL Original
* **Método/Rota:** `GET http://localhost:8080/api/v1/url/{shortKey}`
* **Descrição:** Ao acessar este link direto pelo navegador, o sistema incrementa o contador de acessos e redireciona (Status 302) o usuário automaticamente para o site original.

---

## 🛡️ Diferenciais Técnicos Implementados

1. **Estratégia de Cache Eficiente:** A busca por chaves curtas utiliza a anotação `@Cacheable` do Spring, garantindo que requisições repetidas não sobrecarreguem o banco de dados.
2. **Prevenção de Cache de Valores Nulos:** O cache está configurado para não registrar buscas falhas (`unless`), evitando que requisições inválidas persistam na memória.
3. **Mecanismo de TTL (Time-To-Live):** Configuração customizada do ecossistema Redis para expirar registros de cache automaticamente após 1 dia, otimizando o uso de memória.
4. **Resiliência de Serialização:** Configuração com suporte nativo a tipos modernos do Java 8+ (`ZonedDateTime`) via Jackson Modules.
5. **Validação de Payload:** Uso de validações restritivas (`@NotBlank` e `@URL`) que exigem a presença do protocolo para garantir a integridade dos dados de entrada.

* **Interface Web do Usuário (Frontend):**  
  [http://localhost:8080](http://localhost:8080)  
  *A aplicação conta com uma interface simples, limpa e responsiva nativa. Basta acessar a raiz do projeto no navegador para colar uma URL longa, gerar o link encurtado com apenas um clique e testar o redirecionamento em tempo real de forma visual.*
