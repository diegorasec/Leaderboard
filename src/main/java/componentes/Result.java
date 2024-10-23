package componentes;

public class Result {
	
	private Wod wod;
	private Equipe equipe;
	private String result;
	private Integer position;
	private Integer points;

	public Wod getWod() {
		return wod;
	}
	public void setWod(Wod wod) {
		this.wod = wod;
	}
	public String getResult() {
		return result;
	}
	
	public Integer getResultComp() {
		if(wod.getTipo().getId().equals(1)) {
			return Integer.valueOf(result.substring(0, 2)) * 60
			+ Integer.valueOf(result.substring(3, 5));			
		} else {
			return Integer.valueOf(result);	
		}
		 
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	
	public Equipe getEquipe() {
		return equipe;
	}
	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}
	public Result(Wod wod, Equipe equipe, String result, Integer position, Integer points) {
		super();
		this.wod = wod;
		this.equipe = equipe;
		this.result = result;
		this.position = position;
		this.points = points;
	}
	
	public Result(Wod wod, Equipe equipe, String result) {
		super();
		this.wod = wod;
		this.equipe = equipe;
		this.result = result;
	}
}
