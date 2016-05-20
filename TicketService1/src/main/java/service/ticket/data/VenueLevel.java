package service.ticket.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="VENUELEVEL")
public class VenueLevel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9087164359354078048L;

	@Id
	@Column(name="LEVELID")
	private Integer levelId;

	@Column(name="LEVELNAME")
	private String levelName;
	
	@Column(name="LEVELPRICE")
	private String levelPrice;
	
	@Column(name="LEVELROW")
	private Integer levelRow;
	
	@Column(name="LEVELSEATSPERROW")
	private Integer levelSeatsPerRow;
	
	/**
	 * @return the levelName
	 */
	public String getLevelName() {
		return levelName;
	}
	/**
	 * @param levelName the levelName to set
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	/**
	 * @return the levelId
	 */
	public Integer getLevelId() {
		return levelId;
	}
	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}
	/**
	 * @return the levelPrice
	 */
	public String getLevelPrice() {
		return levelPrice;
	}
	/**
	 * @param levelPrice the levelPrice to set
	 */
	public void setLevelPrice(String levelPrice) {
		this.levelPrice = levelPrice;
	}
	/**
	 * @return the levelRow
	 */
	public Integer getLevelRow() {
		return levelRow;
	}
	/**
	 * @param levelRow the levelRow to set
	 */
	public void setLevelRow(Integer levelRow) {
		this.levelRow = levelRow;
	}
	/**
	 * @return the levelSeatsPerRow
	 */
	public Integer getLevelSeatsPerRow() {
		return levelSeatsPerRow;
	}
	/**
	 * @param levelSeatsPerRow the levelSeatsPerRow to set
	 */
	public void setLevelSeatsPerRow(Integer levelSeatsPerRow) {
		this.levelSeatsPerRow = levelSeatsPerRow;
	}
	
	
	
}
