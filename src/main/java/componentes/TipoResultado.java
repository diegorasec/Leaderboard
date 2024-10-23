package componentes;

public class TipoResultado {
	
	private Integer id;
	private String tipo;
	//Somente em casos de tempo
	private Integer fatorMult;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getFatorMult() {
		return fatorMult;
	}
	public void setFatorMult(Integer fatorMult) {
		this.fatorMult = fatorMult;
	}
	public TipoResultado(Integer id, String tipo) {
		super();
		if(tipo.equals("FOR TIME")) {
			this.fatorMult = 60;	
		} else {
			this.fatorMult = 1;
		}
		this.id = id;
		this.tipo = tipo;
	}
}
