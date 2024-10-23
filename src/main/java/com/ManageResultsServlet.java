package com;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/exemploPost")
public class ManageResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Obtendo o valor do campo 'nome' enviado no formulário
        String nome = request.getParameter("nome");
        
        // Aqui você pode processar o dado, por exemplo, imprimir no console
        System.out.println("Nome recebido: " + nome);

        // Retornar uma resposta
        response.getWriter().println("Nome recebido: " + nome);
    }
}