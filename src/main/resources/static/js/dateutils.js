/**
 * 
 */

Date.prototype.getWeekNumber = function() {
	var d = new Date(+this);
	d.setHours(0, 0, 0);
	d.setDate(d.getDate() + 4 - (d.getDay() || 7));
	return Math
			.ceil((((d - new Date(d.getFullYear(), 0, 1)) / 8.64e7) + 1) / 7);
};

Date.prototype.addDays = function(days) {
	var dat = new Date(this.valueOf());
	dat.setDate(dat.getDate() + days);
	return dat;
}
Date.prototype.getMonday = function() {
	var day = this.getDay() || 7;
	if (this !== 1)
		return this.addDays(-1 * (day - 1));
	else
		return this;
}
Date.prototype.toSimpleString = function() {
	var month = (this.getMonth() < 9 ? '0' : '')+ (this.getMonth() +1);
	var day = (this.getDate() < 10 ? '0' : '') + this.getDate();
	var year = this.getFullYear();
	return year + "-" + month + "-" + day;
}

