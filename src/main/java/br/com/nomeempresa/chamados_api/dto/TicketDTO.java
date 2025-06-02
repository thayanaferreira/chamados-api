package br.com.nomeempresa.chamados_api.dto;

import br.com.nomeempresa.chamados_api.model.Categoria;
import br.com.nomeempresa.chamados_api.model.Sentimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class TicketDTO {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Categoria é obrigatória")
    private Categoria categoria;

    @NotNull(message = "Sentimento é obrigatório")
    private Sentimento sentimento;
}
