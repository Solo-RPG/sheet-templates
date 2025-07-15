# 📚 Módulo de Templates – SOLO RPG

Este serviço gerencia **templates de fichas de RPG**, usados para estruturar dinamicamente a criação de personagens em diferentes sistemas (como D&D 5e, Tormenta20, etc). Ele define os campos esperados, tipos de dados, opções válidas e o layout visual.

---

## 🚀 Como rodar localmente

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/solo-rpg-templates.git
   cd solo-rpg-templates
````

2. Instale as dependências:

   ```bash
   pip install -r requirements.txt
   ```

3. Defina as variáveis de ambiente em um `.env`:

   ```
   MONGO_URI=mongodb+srv://<user>:<senha>@cluster.mongodb.net/?retryWrites=true&w=majority
   DATABASE_NAME=solo_rpg
   ```

4. Inicie o servidor:

   ```bash
   uvicorn app.main:app --reload --port 8000
   ```

---

## 🧠 O que este serviço faz?

* Armazena e fornece **templates de sistemas de RPG**.
* Permite criação, atualização, exclusão e listagem de templates.
* Os templates são usados por outros serviços (como o de fichas) para garantir a consistência dos dados.

---

## 🔗 Endpoints disponíveis

### 📦 Templates

| Método | Rota                            | Descrição                               |
| ------ | ------------------------------- | --------------------------------------- |
| GET    | `/api/templates/`               | Lista todos os templates                |
| GET    | `/api/templates/by-name/{name}` | Busca template por nome do sistema      |
| GET    | `/api/templates/by-id/{id}`     | Busca template por ID                   |
| GET    | `/api/templates/{name}/fields`  | Retorna apenas os campos do template    |
| POST   | `/api/templates/`               | Cria um novo template                   |
| PUT    | `/api/templates/{name}`         | Atualiza um template pelo nome          |
| DELETE | `/api/templates/{name}`         | Deleta um template pelo nome do sistema |
| DELETE | `/api/templates/by-id/{id}`     | Deleta um template pelo ID              |

---

## 🧩 Estrutura de um Template

### Modelo Pydantic

```python
class TemplateField(BaseModel):
    name: str
    type: Literal["string", "number", "boolean", "list", "object"]
    required: bool = True
    default: Optional[Union[str, int, float, bool, list, dict]] = None
    options: Optional[List[str]] = None
    fields: Optional[List["TemplateField"]] = None
```

Um campo pode conter outros campos (ex: atributos → força → valor).

---

## 🧱 Estrutura do Template

Um template tem os seguintes campos:

```json
{
  "system_name": "D&D 5e",
  "version": "1.0",
  "fields": [
    {
      "name": "nome",
      "type": "string",
      "required": true,
      "default": "Novo Personagem"
    },
    {
      "name": "classe",
      "type": "string",
      "required": true,
      "default": "guerreiro",
      "options": ["guerreiro", "mago", "ladino"]
    },
    {
      "name": "atributos",
      "type": "object",
      "required": true,
      "fields": [
        {
          "name": "força",
          "type": "object",
          "fields": [
            { "name": "valor", "type": "number", "default": 10 },
            { "name": "bônus", "type": "number", "default": 0 }
          ]
        }
      ]
    }
  ],
  "template_json": {
    "blocos": [
      { "titulo": "Identificação", "campos": ["nome", "classe"] },
      { "titulo": "Atributos", "campos": ["atributos"] }
    ]
  }
}
```

---

## 📐 Swagger / Documentação da API

Acesse:

```
http://localhost:8000/docs
```

---

## ⚙️ Estrutura de Arquivos

```
templates/
├── app/
│   ├── routers/
│   │   └── templates.py       # Endpoints do serviço
│   ├── models.py              # Modelos de dados (Pydantic)
│   ├── config.py              # Configurações e variáveis de ambiente
│   ├── database.py            # Conexão com MongoDB
│   └── main.py                # Inicialização do FastAPI
```