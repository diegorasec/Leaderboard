package componentes;

public class Wod {
	
	private Integer id;
	private String nome;
	private String descricao;
	private TipoResultado tipo;
	
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
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public TipoResultado getTipo() {
		return tipo;
	}
	public void setTipo(TipoResultado tipo) {
		this.tipo = tipo;
	}
	public Wod(Integer id, String nome, String descricao, TipoResultado tipo) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.tipo = tipo;
	}
	
}
