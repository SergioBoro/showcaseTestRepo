dojo.provide("course.geo.gfx.AnimatedControl");

dojo.require("dojo.fx.easing");

(function(){
	
var DEFAULT_DURATION = 400,	// ms
	DEFAULT_EASING   = dojo.fx.easing.backOut;

dojo.declare("course.geo.gfx.AnimatedControl", null, {
	
	constructor: function(kwArgs) {
		dojo.mixin(this, kwArgs);
	},

	init: function() {
		if (!this.duration) this.duration = DEFAULT_DURATION;
		if (!this.easing) this.easing = DEFAULT_EASING;
	}
});

})();
