package com.escola;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import com.escola.service.MatriculaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Servidor Web embutido utilizando com.sun.net.httpserver.HttpServer do JDK.
 * Oferece uma API REST JSON de alto desempenho e serve a aplicação web frontend SPA.
 */
public class WebServer {

    private static HttpServer server;

    public static void start(MatriculaService service) {
        try {
            int port = 8080;
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Handler para a página HTML do Frontend (SPA)
            server.createContext("/", new HtmlHandler());

            // Handler para a API REST
            server.createContext("/api", new ApiHandler(service));

            server.setExecutor(null); // Cria um executor padrão
            server.start();
            System.out.println("\n\u001B[32m✔ [Web Server] Portal administrativo rodando com sucesso em http://localhost:" + port + "\u001B[0m");
        } catch (IOException e) {
            System.err.println("\u001B[31m❌ Falha ao iniciar o servidor Web: " + e.getMessage() + "\u001B[0m");
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * Handler responsável por servir o frontend SPA (Single Page Application).
     */
    private static class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Método não permitido", "text/plain");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            // Garante que qualquer rota não-API resolva para o index.html (comportamento de SPA)
            if (path.startsWith("/api")) {
                return;
            }

            String htmlContent = loadIndexHtml();
            sendResponse(exchange, 200, htmlContent, "text/html; charset=utf-8");
        }
    }

    /**
     * Handler responsável por processar todas as chamadas de API REST.
     */
    private static class ApiHandler implements HttpHandler {
        private final MatriculaService service;

        public ApiHandler(MatriculaService service) {
            this.service = service;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Adiciona cabeçalhos de CORS caso queira rodar front separado
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            try {
                if (method.equalsIgnoreCase("GET")) {
                    if (path.equals("/api/alunos")) {
                        listarAlunos(exchange);
                    } else if (path.equals("/api/cursos")) {
                        listarCursos(exchange);
                    } else if (path.equals("/api/matriculas")) {
                        listarMatriculas(exchange);
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Endpoint não encontrado\"}", "application/json");
                    }
                } else if (method.equalsIgnoreCase("POST")) {
                    String body = readRequestBody(exchange);
                    if (path.equals("/api/alunos")) {
                        cadastrarAluno(exchange, body);
                    } else if (path.equals("/api/cursos")) {
                        cadastrarCurso(exchange, body);
                    } else if (path.equals("/api/matriculas")) {
                        matricular(exchange, body);
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Endpoint não encontrado\"}", "application/json");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}", "application/json");
                }
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            } catch (IllegalStateException e) {
                sendResponse(exchange, 409, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Erro interno: " + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }

        private void listarAlunos(HttpExchange exchange) throws IOException {
            List<Aluno> alunos = service.listarAlunos();
            String json = "[" + alunos.stream().map(WebServer::alunoToJson).collect(Collectors.joining(",")) + "]";
            sendResponse(exchange, 200, json, "application/json; charset=utf-8");
        }

        private void listarCursos(HttpExchange exchange) throws IOException {
            List<Curso> cursos = service.listarCursos();
            String json = "[" + cursos.stream().map(WebServer::cursoToJson).collect(Collectors.joining(",")) + "]";
            sendResponse(exchange, 200, json, "application/json; charset=utf-8");
        }

        private void listarMatriculas(HttpExchange exchange) throws IOException {
            List<Matricula> matriculas = service.listarMatriculas();
            String json = "[" + matriculas.stream().map(WebServer::matriculaToJson).collect(Collectors.joining(",")) + "]";
            sendResponse(exchange, 200, json, "application/json; charset=utf-8");
        }

        private void cadastrarAluno(HttpExchange exchange, String body) throws IOException {
            Pattern namePattern = Pattern.compile("\"nome\"\\s*:\\s*\"([^\"]+)\"");
            Pattern emailPattern = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");

            Matcher nameMatcher = namePattern.matcher(body);
            Matcher emailMatcher = emailPattern.matcher(body);

            if (!nameMatcher.find() || !emailMatcher.find()) {
                throw new IllegalArgumentException("Campos 'nome' e 'email' são obrigatórios.");
            }

            String nome = nameMatcher.group(1);
            String email = emailMatcher.group(1);

            Aluno aluno = service.cadastrarAluno(nome, email);
            sendResponse(exchange, 201, alunoToJson(aluno), "application/json; charset=utf-8");
        }

        private void cadastrarCurso(HttpExchange exchange, String body) throws IOException {
            Pattern namePattern = Pattern.compile("\"nome\"\\s*:\\s*\"([^\"]+)\"");
            Pattern vagasPattern = Pattern.compile("\"vagas\"\\s*:\\s*(\\d+)");

            Matcher nameMatcher = namePattern.matcher(body);
            Matcher vagasMatcher = vagasPattern.matcher(body);

            if (!nameMatcher.find() || !vagasMatcher.find()) {
                throw new IllegalArgumentException("Campos 'nome' e 'vagas' são obrigatórios.");
            }

            String nome = nameMatcher.group(1);
            int vagas = Integer.parseInt(vagasMatcher.group(1));

            Curso curso = service.cadastrarCurso(nome, vagas);
            sendResponse(exchange, 201, cursoToJson(curso), "application/json; charset=utf-8");
        }

        private void matricular(HttpExchange exchange, String body) throws IOException {
            Pattern emailPattern = Pattern.compile("\"emailAluno\"\\s*:\\s*\"([^\"]+)\"");
            Pattern cursoPattern = Pattern.compile("\"nomeCurso\"\\s*:\\s*\"([^\"]+)\"");

            Matcher emailMatcher = emailPattern.matcher(body);
            Matcher cursoMatcher = cursoPattern.matcher(body);

            if (!emailMatcher.find() || !cursoMatcher.find()) {
                throw new IllegalArgumentException("Campos 'emailAluno' e 'nomeCurso' são obrigatórios.");
            }

            String emailAluno = emailMatcher.group(1);
            String nomeCurso = cursoMatcher.group(1);

            Matricula matricula = service.matricular(emailAluno, nomeCurso);
            sendResponse(exchange, 201, matriculaToJson(matricula), "application/json; charset=utf-8");
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            try (InputStream is = exchange.getRequestBody()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
    }

    private static String loadIndexHtml() {
        // Tenta ler do classpath
        try (InputStream is = WebServer.class.getResourceAsStream("/web/index.html")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}

        // Fallback para desenvolvimento local caso rode diretamente na pasta de trabalho
        try {
            java.nio.file.Path localPath = java.nio.file.Paths.get("src", "main", "resources", "web", "index.html");
            if (java.nio.file.Files.exists(localPath)) {
                return java.nio.file.Files.readString(localPath, StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}

        // Último recurso: HTML minimalista se nada for encontrado
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Sistema de Matrículas</title></head>"
                + "<body style='font-family:sans-serif; text-align:center; padding-top:100px; background:#f4f4f5; color:#3f3f46;'>"
                + "<h1>Sistema de Matrículas - Escola de Idiomas</h1>"
                + "<p>O arquivo frontend 'index.html' não foi encontrado nos recursos do servidor.</p>"
                + "<p>Certifique-se de compilar os recursos corretamente ou verificar as pastas.</p></body></html>";
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // --- Auxiliares de Serialização JSON Simplificada ---
    private static String alunoToJson(Aluno a) {
        return String.format("{\"nome\":\"%s\",\"email\":\"%s\"}",
                escapeJson(a.getNome()), escapeJson(a.getEmail()));
    }

    private static String cursoToJson(Curso c) {
        return String.format("{\"nome\":\"%s\",\"vagasTotais\":%d,\"vagasDisponiveis\":%d}",
                escapeJson(c.getNome()), c.getVagasTotais(), c.getVagasDisponiveis());
    }

    private static String matriculaToJson(Matricula m) {
        return String.format("{\"aluno\":%s,\"curso\":%s}",
                alunoToJson(m.getAluno()), cursoToJson(m.getCurso()));
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
