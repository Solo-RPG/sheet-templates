from pydantic import BaseModel, Field
from typing import List, Optional, Union, Literal, Dict, Any, ForwardRef

# Referência futura (porque TemplateField precisa referenciar a si mesmo)
TemplateFieldRef = ForwardRef("TemplateField")

class TemplateField(BaseModel):
    name: str
    type: Literal["string", "number", "boolean", "list", "object"]
    required: bool = True
    default: Optional[Union[str, int, float, bool, list, dict]] = None
    options: Optional[List[str]] = None
    fields: Optional[List["TemplateField"]] = None  # Aqui está a recursividade

# Resolve a referência recursiva
TemplateField.model_rebuild()

class TemplateCreate(BaseModel):
    system_name: str = Field(..., min_length=3, description="Nome do sistema RPG")
    version: str = Field(default="1.0", description="Versão do template")
    fields: List[TemplateField]
    template_json: Dict[str, Any]

class TemplateResponse(BaseModel):
    id: str
    system_name: str
    version: str
    fields: List[TemplateField]
    template_json: Dict[str, Any]
    