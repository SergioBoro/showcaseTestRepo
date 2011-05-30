dojo.provide("course.geo.Popup");

dojo.require("dijit.Tooltip");
dojo.require("dijit.layout.ContentPane");

(function() {
    
var defaultPosition = ["above", "below"];

dojo.declare("course.geo.Popup", [dijit.layout.ContentPane, dijit._MasterTooltip], {

    templateString: dojo.cache("course.geo", "templates/Popup.html"),
    
    open: false,
    
    lonlat: null,
    
    doLayout: false,

    offsetX: 11,

    offsetY: -6,
    
    lonLatTransform: null,
    
    lonLatToXY: null,
    
    // a constant parameter for dijit placement functions
    _layoutNode: null,
    
    // a variable parameter for dijit placement functions
    _aroundCorners: null,
    
    constructor: function() {
        this._layoutNode = dojo.hitch(this, "orient");
    },
    
    show: function(/*String*/ innerHTML, /*Object*/ lonlat, /*String[]?*/ position, /*Boolean*/ rtl) {
        if (this.lonLatTransform) lonlat = this.lonLatTransform(lonlat);
        this.lonlat = lonlat;
        this._show(innerHTML, this.lonLatToXY(lonlat), position, rtl);
    },

    show: function(/*String*/ innerHTML, /*DomNode||Object*/ lonlat, /*String[]?*/ position, /*Boolean*/ rtl) {
        // summary:
        //		Display tooltip w/specified contents to right of specified node
        //		(To left if there's no space on the right, or if rtl == true)
    
        //if(this.aroundNode && this.aroundNode === aroundNode){
        //    return;
        //}
    
        if(this.fadeOut.status() == "playing"){
                // previous tooltip is being hidden; wait until the hide completes then show new one
                this._onDeck=arguments;
                return;
        }
        //this.containerNode.innerHTML=innerHTML;
        this.set("content", innerHTML);
        //this.set("href", "test-content.html");
        
        // perform lonlat transform if needed
        if (this.lonLatTransform) lonlat = this.lonLatTransform(lonlat);
        // transform lonlat to screen coords
        var aroundNode = this.lonLatToXY(lonlat);
        //var pos = dijit.placeOnScreenAroundNode(this.domNode, aroundNode, dijit.getPopupAroundAlignment((position && position.length) ? position : dijit.Tooltip.defaultPosition, !rtl), dojo.hitch(this, "orient"));
        this._aroundCorners = dijit.getPopupAroundAlignment((position && position.length) ? position : defaultPosition, !rtl);
        dijit._placeOnScreenAroundRect(this.domNode, aroundNode.x-this.offsetX, aroundNode.y-this.offsetY, 0, 0, this._aroundCorners, this._layoutNode);
        // show it
        dojo.style(this.domNode, "opacity", 0);
        this.fadeIn.play();
        this.isShowingNow = true;
        this.lonlat = lonlat;
        this.aroundNode = aroundNode;
    },
    
    hide: function(){
        // summary:
        //		Hide the tooltip
        if(this._onDeck){
            // this hide request is for a show() that hasn't even started yet;
            // just cancel the pending show()
            this._onDeck=null;
        }else if(this.aroundNode){
            // this hide request is for the currently displayed tooltip
            this.fadeIn.stop();
            this.isShowingNow = false;
            this.aroundNode = null;
            this.fadeOut.play();
        }else{
            // just ignore the call, it's for a tooltip that has already been erased
        }
    },
    
    _onShow: function() {
        this.open = true;
        this.inherited("_onShow", arguments);
    },
    
    _onHide: function() {
        this.inherited("_onHide", arguments);
        this.open = false;
    },
    
    updatePosition: function() {
        if (!this.open) return;

        var aroundNode = this.lonLatToXY(this.lonlat);
        dijit._placeOnScreenAroundRect(this.domNode, aroundNode.x-this.offsetX, aroundNode.y-this.offsetY, 0, 0, this._aroundCorners, this._layoutNode);
        this.aroundNode = aroundNode;
    }

});

})();