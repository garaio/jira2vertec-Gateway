var SELECT_PROJEKT = "#selectProjekt"; 
var SELECT_PHASE = "#selectPhase";
var CUSTOM_FIELD = "";
var CUSTOM_FIELD_CB = "";

var _baseUrl  = "";
var _initialProjektId  = "";
var _initialPhaseId  = "";

function initVertecToJira(baseUrl, customFieldId, issueProjektId, issuePhaseId) {
    CUSTOM_FIELD = "#" + customFieldId;
    CUSTOM_FIELD_CB = "#cb" + customFieldId;

    _baseUrl = baseUrl;
    _initialProjektId = issueProjektId;
    _initialPhaseId = AJS.$(CUSTOM_FIELD).val();

    if (!IsCustomFieldAvailable()) {
        // In diesem Fall ist nicht die erwartete Form des CustomFields vorhanden
        return;
    }

    if (_initialPhaseId === "null") {
       _initialPhaseId = "";  
    }

    var phaseSelect = AJS.$(SELECT_PHASE);

    phaseSelect.attr('disabled', 'disabled');
    phaseSelect.change(function(){
        PhaseSelected();
    });
	
    AJS.$(SELECT_PROJEKT).change(function(){
        _initialPhaseId = "";
        ProjektSelected();
    });

    // Falls keine ProjektId mitgegeben wurde oder die Phase des Issues anders ist als diejenige im CustomFiled,
    // dann muss das Projekt anhand der Phase geladen werden
    if (IsNullOrEmpty(_initialProjektId) || issuePhaseId != _initialPhaseId){
        LadeProjektIdVonPhaseId();
    } else {
        ReloadProjekte();
    }
}

function IsCustomFieldAvailable() {
    return typeof AJS.$(CUSTOM_FIELD).val() != "undefined";
}

function IsNullOrEmpty(value) {
    return !value || value === "null" || value.length < 1 || value < 1;
}

function ReloadProjekte() {
    if (!IsCustomFieldAvailable()) {
        return;
    }

    AJS.$(SELECT_PHASE).attr('disabled', 'disabled');

    var projektSelect = AJS.$(SELECT_PROJEKT);
    projektSelect.attr('disabled', 'disabled');

	AJS.$.ajax({
	url: _baseUrl + '/rest/vertec/1.0/vertec/projekte',
	type: 'get',
	dataType: 'json',
	async: true,
	success: function(data) {
        var items = [];

        items.push('<option value="">-- Keine Zuordnung --</option>');

        AJS.$.each(data, function(key, val) {
            items.push('<option value="' + val.objid + '">' + val.bezeichnung + '</option>');
        });

        projektSelect.html(items.join(''));
        if (_initialProjektId == "-1" && _initialPhaseId.length > 0) {
            // wenn zur Phase kein Projekt gefunden wurde
            items.push('<option value="' + _initialProjektId + '">' + "-- Ung&uuml;ltige Phase --" + '</option>');
        }
        else if (_initialPhaseId.length > 0 && AJS.$(SELECT_PROJEKT + " option[value='" + _initialProjektId + "']").length < 1) {
            // wenn aktuell ausgewähltes Projekt nicht in der Dropdown ist
            items.push('<option value="' + _initialProjektId + '">' + "-- Projekt deaktiviert --" + '</option>');
        }
        projektSelect.html(items.join(''));
        projektSelect.removeAttr('disabled', 'disabled');
        projektSelect.val(_initialProjektId);
        //projektSelect.change();

        if (AJS.$(CUSTOM_FIELD_CB).length > 0)    {
            // Checkbox bei Massenveränderungen deselektieren
            AJS.$(CUSTOM_FIELD_CB).removeAttr('checked')  ;
        }
		
        ProjektSelected();
		
    }});
}

function PhaseSelected(){
    var phaseId = AJS.$(SELECT_PHASE).val();
    if (!phaseId)
        phaseId = "";
    AJS.$(CUSTOM_FIELD).val(phaseId);
}

function LadeProjektIdVonPhaseId(){
		AJS.$.ajax({
        url: _baseUrl + '/rest/vertec/1.0/vertec/projekte/' + _initialPhaseId,
        type: 'get',
        dataType: 'json',
        async: false,
        success: function(data) {
            if (typeof data != undefined && data != null && data.objid != undefined && data.objid != null && data.objid > 0) {
			    _initialProjektId = data.objid;
            }
			ReloadProjekte();
		}
    });
}

function ProjektSelected(){
    if (!IsCustomFieldAvailable()) {
        return;
    }
    var projektId = AJS.$(SELECT_PROJEKT).val();

    //Phase-Dropdown leeren und disablen
    var phaseSelect = AJS.$(SELECT_PHASE);
    phaseSelect.html("");
    phaseSelect.attr('disabled', 'disabled');

    if (projektId === "") {
        PhaseSelected();    
    }  else {	
		AJS.$.ajax({
        url: _baseUrl + '/rest/vertec/1.0/vertec/phasen/' + projektId,
        type: 'get',
        dataType: 'json',
        async: true,
        success: function(data) {		
            var items = [];

            AJS.$.each(data, function(key, val) {
                items.push('<option value="' + val.objid + '">' + val.bezeichnung + '</option>');
            });		

            phaseSelect.removeAttr('disabled');
            phaseSelect.html(items.join(''));
            if (_initialPhaseId.length > 0){
                if (_initialProjektId == "-1" && _initialPhaseId.length > 0) {
                    // wenn zur Phase kein Projekt gefunden wurde
                    items.push('<option value="' + _initialPhaseId + '">' + "-- Ung&uuml;ltige Phase --" + '</option>');
                    phaseSelect.html(items.join(''));
                } else if (AJS.$(SELECT_PHASE + " option[value='" + _initialPhaseId + "']").length < 1) {
                    // wenn aktuell ausgewählte Phase nicht in der Dropdown ist
                    items.push('<option value="' + _initialPhaseId + '">' + "-- Phase deaktiviert --" + '</option>');
                    phaseSelect.html(items.join(''));
                }

                phaseSelect.val(_initialPhaseId);
            }
            else {
                PhaseSelected();
            }
		}
		});
    }
}