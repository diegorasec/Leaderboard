package com;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.opencsv.CSVReader;

import componentes.Categoria;
import componentes.Equipe;
import componentes.Result;
import componentes.TipoResultado;
import componentes.Wod;

public class Core {

	static String PATH_RESOURCES = "C:\\Users\\dcesar\\eclipse-workspace\\Leaderboard\\src\\main\\resources\\";

	public static void main(String[] args)  {
		Runnable task = () -> {
			try {
				while (true) {
					Map<Categoria, List<Equipe>> lead = carregaLeaderboard();
					calculaPontos(lead);
					String filePath = "leaderboard.html";
					criaLeaderboard(filePath, lead);
			        String caminhoDoBat = "C:\\Users\\dcesar\\eclipse-workspace\\Leaderboard\\autoPush.bat";
			        ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/c", caminhoDoBat));
			        builder.inheritIO();
			        builder.start(); 
					Thread.sleep(120000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread thread = new Thread(task);
		thread.start();
					
	}


	public static void criaLeaderboard(String filePath, Map<Categoria, List<Equipe>> lead) {
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append(renderHeaderCSS());
		Iterator<Map.Entry<Categoria, List<Equipe>>> iterator = lead.entrySet().iterator();
		while (iterator.hasNext()) {
			int i = 1;
			Map.Entry<Categoria, List<Equipe>> entry = iterator.next();
			//	Categoria categoria = entry.getKey();
			List<Equipe> equipeOpt = entry.getValue();
			/*htmlContent.append("<div id=\""+categoria.getNome()+"\" style=\"display:block\";>");
			htmlContent.append("<h2>"+categoria.getNome()+"</h2>");*/
			htmlContent.append(commomHeadTable());
			htmlContent.append("    <tbody>\n");
			
			Collections.sort(equipeOpt, new Comparator<Equipe>() {
				@Override
				public int compare(Equipe t1, Equipe t2) {
					return t1.getPoints().compareTo(t2.getPoints());
				}
			});
			Integer pointsLast = 0;
			Integer positionLast = 0;
			for (Equipe e : equipeOpt) {
				if(pointsLast > 0 && positionLast > 0 &&
						e.getPoints().equals(pointsLast)) {
					htmlContent.append("<tr>\n")
					.append("            <td>" + positionLast + "</td>\n")
					.append("            <td>" + e.getNome() + "</td>\n")
					.append("            <td> "+ e.getPoints() + " </td>\n");
				} else {
					htmlContent.append("<tr>\n")
					.append("            <td>" + i + "</td>\n")
					.append("            <td>" + e.getNome() + "</td>\n")
					.append("            <td> "+ e.getPoints() + " </td>\n");
					positionLast = i;
				}
				String kgs="";
				for(Result r : e.getResult()) {
					if(r.getWod().getTipo().getId() == 3) {
						kgs = "kgs";
					}
					if(r.getPosition().equals(1)) {
						htmlContent.append(" <td style=\"color: #bd9422; font-size: 16px; font-weight: bold;\"> ");
						htmlContent.append("("+r.getPosition()+") "+r.getResult()+ " " + kgs + "</td>\n");
					} else {
						htmlContent.append(" <td style=\"color: #fff; font-size: 16px;\">("+r.getPosition()+") "+r.getResult()+" </td>\n");	
						
					}
				}
				htmlContent.append("</tr>\n");
				pointsLast = e.getPoints();
				i++;
			}
			htmlContent.append("        </tr>\n")
			            .append("    </tbody>\n")
			            .append("</table>\n")
			            .append("</div>\n");			
		}
		
		htmlContent.append(renderFooter());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(htmlContent.toString());
		} catch (IOException e) {
			System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
		}

	}

	public static void calculaPontos(Map<Categoria, List<Equipe>> lead) {
		Iterator<Map.Entry<Categoria, List<Equipe>>> iterator = lead.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Categoria, List<Equipe>> entry = iterator.next();
			Categoria categoria = entry.getKey();
			List<Equipe> equipeOpt = entry.getValue();
			for(Wod w : readCSVWods()) {
				List<Result> auxCalc = new ArrayList<Result>();
				for(Equipe e : equipeOpt) {
					if(categoria.getId().equals(e.getCategoria().getId())) {
						for(Result r : e.getResult()) {
							if(r.getWod().getId().equals(w.getId())) {
								Result auxRes = new Result(w, e, r.getResult());
								auxCalc.add(auxRes);
							}
						}
					}
				}
				if(auxCalc.size() > 0 ) {
					Collections.sort(auxCalc, new Comparator<Result>() {
						@Override
						public int compare(Result t1, Result t2) {
							return t1.getResultComp().compareTo(t2.getResultComp());
						}
					});
					if(w.getTipo().getId() != 1) {
						auxCalc.sort(Comparator.comparing(Result::getResultComp).reversed());						
					}
					
					int i = 1;
					Integer pointsLast = 0;
					Integer positionLast = 0;
					Integer resultLast = 0;
					for(Result r : auxCalc) {
						for(Equipe e :equipeOpt) {
							if(r.getEquipe().getId().equals(e.getId())) {
								Integer pts = e.getPoints();
								for(Result re : e.getResult()) {
									if(re.getEquipe().getId().equals(r.getEquipe().getId()) &&
											re.getWod().getId().equals(w.getId())) {
										
										if(pointsLast > 0 && positionLast > 0 && resultLast > 0
												&& re.getResultComp().equals(resultLast)) {
											re.setPoints(pointsLast);
											re.setPosition(positionLast);
											e.setPoints(pts + pointsLast);
										} else {
											re.setPoints(i);
											re.setPosition(i);
											e.setPoints(pts + i);
										}
										pointsLast = re.getPoints();
										positionLast = re.getPosition();
										resultLast = re.getResultComp();
									}
								}
								i++;
							}
						}
					}
				}
			}
		}
	}

	public static String renderHeaderCSS() {
		StringBuilder head = new StringBuilder();
		head.append("<!DOCTYPE html>\r\n")
		.append("<html lang=\"pt-BR\">\r\n")
		.append("<head>\r\n")
		.append("    <meta charset=\"UTF-8\">\r\n")
		.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n")
		.append("    <title>PENTA  League</title>\r\n")
		.append("    <link rel=\"icon\" href=\"src/main/resources/iconLeague.ico\" type=\"image/x-icon\">\r\n")
		.append("    <style>\r\n")
		.append("        body {\r\n")
		.append("            font-family: Arial, sans-serif;\r\n")
		.append("            color:#fff;\r\n")
		.append("            background-color: #1C1F22;\r\n")
		.append("            margin: 0;\r\n")
		.append("            padding: 20px;\r\n")
		.append("        }\r\n")
		.append("        table {\r\n")
		.append("            width: 100%;\r\n")
		.append("            border-collapse: collapse;\r\n")
		.append("            margin: 10px 0;\r\n")
		.append("            background-color: #26292B;\r\n")
		.append("            box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);\r\n")
		.append("        }\r\n")
		.append("        th, td {\r\n")
		.append("            padding: 12px;\r\n")
		.append("            text-align: center;\r\n")
		.append("            border: 1px solid #44484D;\r\n")
		.append("        }\r\n")
		.append("        th {\r\n")
		.append("            background-color: #FF3819;\r\n")
		.append("            color: #FFFFFF;\r\n")
		.append("            cursor: pointer;\r\n")
		.append("        }\r\n")
		.append("        tr:nth-child(even) {\r\n")
		.append("            background-color: #32393E;\r\n")
		.append("        }\r\n")
        .append("        img {\n")
        .append("            width: 10px;\n") 
        .append("            height: 18px;\n")
        .append("        }\n")
        .append(" button {\n")
        .append("	padding: 10px 15px;\n")
        .append("	margin: 5px;\n")
        .append("	background-color: #FF3819;\n")
        .append("	color: #FFFFFF;\n")
        .append("	border: none;\n")
        .append("	cursor: pointer;\n")
        .append("   transition: background-color 0.3s ease; \n")
        .append("}\n")
        .append(" button:hover {\n")
        .append("	background-color: #FF5733;\n")
        .append("} \n")
        
		.append("    </style>\r\n")
		.append("</>\r\n")
		.append("<body>\r\n")
		.append("\r\n")
		.append("<div style=\"display: flex; align-items: center; justify-content: center; background-color: #FF3819;\">\r\n"
				+ "    <h1 style=\"display: flex; align-items: center; color: #FFFFFF;\">\r\n"
				+ "        <img src=\"src/main/resources/logoPentaLeague.png\" alt=\"Logo\" style=\"width: 100px; height:100px;\">\r\n"
				+ "    </h1>\r\n"
				+ "</div>");
		/*
		.append(" <button onclick=\"showDiv('Iniciante')\">Iniciante</button> \n")
		.append(" <button onclick=\"showDiv('Scale')\">Scale</button> \n")
		.append(" <button onclick=\"showDiv('RX')\">RX</button> \n");		
		*/
		return head.toString();		
	}
	public static String commomHeadTable() {
		StringBuilder head = new StringBuilder();
		head.append("<table id=\"leadTable\">\n")
		.append("    <thead>\n")
		.append("        <tr>\n")
		.append("            <th onclick=\"sortTable(0)\">RANK</th>\n")
		.append("            <th onclick=\"sortTable(1)\">NAME</th>\n")
		.append("            <th onclick=\"sortTable(2)\">POINTS</th>\n");
		int ind = 3;
		for(Wod w : readCSVWods()) {
			head.append("            <th onclick=\"sortTable("+ind+")\">"+w.getNome()+"</th>\n");
			ind++;
		}
		head.append("        </tr>\n")
		.append("    </thead>\n");
		return head.toString();
	}
	public static String renderFooter() {
		StringBuilder foot = new StringBuilder();
		
		foot.append(" <script>\r\n"
				+ "    function sortTable(columnIndex) {\r\n"
				+ "        var table, rows, switching, i, x, y, shouldSwitch, direction, switchcount = 0;\r\n"
				+ "        table = document.getElementById(\"leadTable\");\r\n"
				+ "        switching = true;\r\n"
				+ "        direction = \"asc\"; // Começa com ordem ascendente\r\n"
				+ "\r\n"
				+ "        while (switching) {\r\n"
				+ "            switching = false;\r\n"
				+ "            rows = table.rows;\r\n"
				+ "\r\n"
				+ "            // Percorre as linhas da tabela\r\n"
				+ "            for (i = 1; i < (rows.length - 1); i++) {\r\n"
				+ "                shouldSwitch = false;\r\n"
				+ "                \r\n"
				+ "                // Obtém os elementos das células a serem comparadas\r\n"
				+ "                x = rows[i].getElementsByTagName(\"TD\")[columnIndex];\r\n"
				+ "                y = rows[i + 1].getElementsByTagName(\"TD\")[columnIndex];\r\n"
				+ "                \r\n"
				+ "                // Extrai o valor dentro dos parênteses\r\n"
				+ "                var xValue = extractValueFromParentheses(x.innerHTML);\r\n"
				+ "                var yValue = extractValueFromParentheses(y.innerHTML);\r\n"
				+ "\r\n"
				+ "                // Compara os valores\r\n"
				+ "                if (direction == \"asc\") {\r\n"
				+ "					if (columnIndex === 1) {\r\n"
				+ "						if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {\r\n"
				+ "							shouldSwitch = true;\r\n"
				+ "							break;\r\n"
				+ "						}\r\n"
				+ "					} else {\r\n"
				+ "						if (parseFloat(xValue) > parseFloat(yValue)) {\r\n"
				+ "							shouldSwitch = true;\r\n"
				+ "							break;\r\n"
				+ "						}\r\n"
				+ "					}\r\n"
				+ "                } else if (direction == \"desc\") {\r\n"
				+ "					if (columnIndex === 1) {\r\n"
				+ "						if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {\r\n"
				+ "							shouldSwitch = true;\r\n"
				+ "							break;\r\n"
				+ "						}\r\n"
				+ "					} else {\r\n"
				+ "						if (parseFloat(xValue) > parseFloat(yValue)) {\r\n"
				+ "							shouldSwitch = true;\r\n"
				+ "							break;\r\n"
				+ "						}\r\n"
				+ "					}\r\n"
				+ "				}\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            if (shouldSwitch) {\r\n"
				+ "                // Faz a troca de posições das linhas\r\n"
				+ "                rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);\r\n"
				+ "                switching = true;\r\n"
				+ "                switchcount++;\r\n"
				+ "            } else {\r\n"
				+ "                // Se não houve troca e a direção era ascendente, muda para descendente\r\n"
				+ "                if (switchcount === 0 && direction === \"asc\") {\r\n"
				+ "                    direction = \"desc\";\r\n"
				+ "                    switching = true;\r\n"
				+ "                }\r\n"
				+ "            }\r\n"
				+ "        }\r\n"
				+ "    }\r\n"
				+ "\r\n"
				+ "    // Função para extrair o valor dentro dos parênteses\r\n"
				+ "    function extractValueFromParentheses(text) {\r\n"
				+ "        var match = text.match(/\\(([^)]+)\\)/);\r\n"
				+ "        return match ? match[1] : text;\r\n"
				+ "    }\r\n"
				+ "</script>");		
		
		
		foot.append("</body>\r\n" + "</html>");
		return foot.toString();
	}

	public static Map<Categoria, List<Equipe>> carregaLeaderboard() {
		Map<Categoria, List<Equipe>> lead = new HashMap<Categoria, List<Equipe>>();
		for (Categoria c : readCSVCategoria()) {
			lead.put(c, carregaEquipes(c.getId()));
		}
		lead.entrySet().removeIf(entry -> entry.getValue().isEmpty());
		return lead;
	}

	public static List<Equipe> carregaEquipes(Integer categId) {
		List<Equipe> equipes = new ArrayList<Equipe>();
		try (CSVReader reader = new CSVReader(new FileReader(PATH_RESOURCES + "equipes.csv"))) {
			reader.readNext();
			String[] line;
			while ((line = reader.readNext()) != null) {
				if (line.length > 0 && categId.equals(Integer.valueOf(line[2]))) {
					Optional<Categoria> foundCategoria = readCSVCategoria().stream()
							.filter(categoria -> categoria.getId().equals(categId)).findFirst();

					Equipe e = new Equipe(Integer.valueOf(line[0]), line[1], foundCategoria.get(),
							Arrays.asList(line[3].split("/")));

					List<Wod> foundWods = readCSVWods();
					List<Result> result = new ArrayList<Result>();
					int wodNum = 1;
					try (CSVReader r = new CSVReader(new FileReader(PATH_RESOURCES + "results.csv"))) {
						r.readNext();
						String[] l;
						while ((l = r.readNext()) != null) {
							for(Wod w : foundWods) {
								if(w.getId().equals(wodNum)  &&
										!l[wodNum+1].isEmpty() &&
										e.getId().equals(Integer.valueOf(l[0]))) {
									Result res = new Result(w, e, l[wodNum+1], 0, 0);
									result.add(res);
									wodNum++;
								}								
							}
						}
					}
					Integer points = 0;
					e.setPoints(points);
					e.setResult(result);
					equipes.add(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return equipes;
	}

	public static List<Categoria> readCSVCategoria() {
		List<Categoria> categorias = new ArrayList<Categoria>();
		try (CSVReader reader = new CSVReader(new FileReader(PATH_RESOURCES + "categorias.csv"))) {
			String[] line;
			reader.readNext();

			while ((line = reader.readNext()) != null) {
				Categoria cat = new Categoria(Integer.valueOf(line[0]), line[1]);
				categorias.add(cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categorias;
	}

	public static List<TipoResultado> readCSVTipoResultado() {
		List<TipoResultado> tipos = new ArrayList<TipoResultado>();
		try (CSVReader reader = new CSVReader(new FileReader(PATH_RESOURCES + "tipoResultado.csv"))) {
			String[] line;
			reader.readNext();

			while ((line = reader.readNext()) != null) {
				TipoResultado cat = new TipoResultado(Integer.valueOf(line[0]), line[1]);
				tipos.add(cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tipos;
	}

	public static List<Wod> readCSVWods() {
		List<Wod> wods = new ArrayList<Wod>();
		try (CSVReader reader = new CSVReader(new FileReader(PATH_RESOURCES + "wods.csv"))) {
			String[] line;
			reader.readNext();

			while ((line = reader.readNext()) != null) {
				if (line.length > 1) {
					Integer tipoId = Integer.valueOf(line[3]);
					Optional<TipoResultado> foundTipo = readCSVTipoResultado().stream()
							.filter(tipo -> tipo.getId().equals(tipoId)).findFirst();

					Wod wod = new Wod(Integer.valueOf(line[0]), line[1], line[2], foundTipo.get());
					wods.add(wod);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wods;
	}

}
