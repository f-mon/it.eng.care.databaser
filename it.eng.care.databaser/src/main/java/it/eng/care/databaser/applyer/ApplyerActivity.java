package it.eng.care.databaser.applyer;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.eng.care.databaser.model.CareChangeSet;
import it.eng.care.databaser.model.CareChangelogFile;
import it.eng.care.databaser.model.CareModule;
import it.eng.care.databaser.model.ChangeLogElement;
import it.eng.care.databaser.service.ChangeLogsService;
import it.fmoon.fxapp.mvc.AbstractActivity;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import liquibase.changelog.ChangeSet;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ApplyerActivity extends AbstractActivity<ApplyerActivityDef> {

	@FXML TreeTableView<ChangeLogElement> treeTableview;
	@FXML TreeTableColumn<ChangeLogElement,ChangeLogElement> treeMainColumn;
	
	@Autowired
	private ChangeLogsService changeLogsService;
	
	@Autowired
	private ExecutorService executorService;

	public ApplyerActivity(ApplyerActivityDef def) {
		super(def);
	}

	@FXML
    public void initialize() {
		TreeItem<ChangeLogElement> root = new TreeItem<ChangeLogElement>(null);
		treeTableview.setRoot(root);
		treeTableview.setShowRoot(false);
		treeTableview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeTableview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ChangeLogElement>>() {
		    @Override
		    public void changed(ObservableValue<? extends TreeItem<ChangeLogElement>> observable, TreeItem<ChangeLogElement> oldValue, TreeItem<ChangeLogElement> newValue) {
		        onSelectElement(newValue);
		    }
		});
		
		
		treeMainColumn.setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<ChangeLogElement>(cellDataFeatures.getValue().getValue())); 
		treeMainColumn.setCellFactory(column -> new TreeTableCell<ChangeLogElement,ChangeLogElement>() {
	        @Override
	        public void updateItem(ChangeLogElement item, boolean empty) {
	            super.updateItem(item, empty);
	            //setDisclosureNode(null);

	            if (empty || item==null) {
	                setText("");
	                setGraphic(null);
	                
	            } else if (item instanceof CareChangelogFile) {
	            	
	            	CareChangelogFile careChangelogFile = (CareChangelogFile)item;
	            	
	            	StringBuilder label = new StringBuilder();
	            	label.append(careChangelogFile.getName());
	            	if (careChangelogFile.hasChangeLogInfo()) {
	            		label.append(" (")
	            			.append(careChangelogFile.getChangeLog().getChangeSets().size())
	            			.append(") ");
	            	}
	            	
	                setText(label.toString());
	                
	            } else if (item instanceof CareModule) {
	                CareModule careModule = (CareModule)item;
					setText(careModule.getModuleName()+" ("+careModule.getChangeLogFiles().size()+")");
					
	            } else if (item instanceof CareChangeSet) {
	            	ChangeSet changeSet = ((CareChangeSet) item).getChangeSet();
	            	setText(changeSet.getId()+" - by "+changeSet.getAuthor());
	            }
	        }

	    });
    }

	@FXML public void onScanChangelogs(ActionEvent event) {
		System.out.println("scan");
		
		Collection<CareModule> modules = changeLogsService.scanAndGetModules();
		
		treeTableview.getRoot().getChildren().clear();
		TreeItem<ChangeLogElement> root = treeTableview.getRoot();
		
		modules.stream().forEach(mod->{
			TreeItem<ChangeLogElement> treeItem = new TreeItem<ChangeLogElement>(mod);
			mod.getChangeLogFiles().stream().forEach(ch->{
				TreeItem<ChangeLogElement> chItem = new TreeItem<ChangeLogElement>(ch);
				treeItem.getChildren().add(chItem);
			});
			root.getChildren().add(treeItem);
		});
	}
	

	public void onSelectElement(TreeItem<ChangeLogElement> changeLogElementTreeItem) {
		ChangeLogElement changeLogElement = (ChangeLogElement)changeLogElementTreeItem.getValue();
		if (changeLogElement instanceof CareChangelogFile) {
			CareChangelogFile changeLogFile = (CareChangelogFile)changeLogElement;
			
			executorService.submit(()->{
				changeLogsService.analyzeChangeLogFile(changeLogFile);
				Platform.runLater(()->{						
					buildSubChangelogTree(changeLogElementTreeItem,changeLogFile);
					Event.fireEvent(changeLogElementTreeItem,new TreeModificationEvent<ChangeLogElement>(TreeItem.valueChangedEvent(),changeLogElementTreeItem));
				});
			});
		}
	}

	private void buildSubChangelogTree(
			TreeItem<ChangeLogElement> changeLogElementTreeItem,
			CareChangelogFile changeLogFile) 
	{
		changeLogElementTreeItem.getChildren().clear();
		changeLogFile.getChangeLog().getChangeSets().stream()
			.map(chS->new CareChangeSet(chS))
			.forEach(chSet->{
				TreeItem<ChangeLogElement> chItem = new TreeItem<ChangeLogElement>(chSet);
				changeLogElementTreeItem.getChildren().add(chItem);
			});
	}

}
