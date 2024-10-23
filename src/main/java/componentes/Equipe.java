package componentes;

import java.util.List;

public class Equipe {

	private Integer id;
	private String nome;
	private Categoria categoria;
	private List<String> membros;
	private List<Result> result;
	private Integer points;
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public List<String> getMembros() {
		return membros;
	}
	public void setMembros(List<String> membros) {
		this.membros = membros;
	}
	public List<Result> getResult() {
		return result;
	}
	public void setResult(List<Result> result) {
		this.result = result;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public Equipe(Integer id,String nome, Categoria categoria, List<String> membros) {
		super();
		this.id = id;
		this.nome = nome;
		this.categoria = categoria;
		this.membros = membros;
	}
}
