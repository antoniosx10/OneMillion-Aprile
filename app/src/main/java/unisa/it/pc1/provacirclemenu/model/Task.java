package unisa.it.pc1.provacirclemenu.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Antonio on 22/03/2018.
 */

public class Task implements Serializable{

    private String taskId;
    private String contenuto;
    private Date data;
    private Date deadline;
    private String descrizione;
    private String categoria;
    private Boolean stato;
    private String from;
    private String isImage;

    private boolean isSelected = false;


    public Task(String contenuto, Date data,String taskId) {
        this.contenuto = contenuto;
        this.data = data;
        this.taskId = taskId;
    }

    public Task(String contenuto, Date data, Date deadline, String descrizione, String categoria,String taskId,Boolean stato,String from,String isImage) {
        this.contenuto = contenuto;
        this.data = data;
        this.deadline = deadline;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.taskId = taskId;
        this.stato = stato;
        this.from = from;
        this.isImage = isImage;
    }

    public Task(){

    }

    public String getIsImage() {
        return isImage;
    }

    public void setIsImage(String isImage) {
        this.isImage = isImage;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String id) {
        taskId = id;
    }


    public Boolean getStato() {
        return stato;
    }

    public void setStato(Boolean stato) {
        this.stato = stato;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}
