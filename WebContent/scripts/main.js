(function() {

	/**
	 * Variables
	 */
	var user_id = '1111';
	var password = '3229c1097c00d497a0fd282d586be050';
	var item_counter = 1;

	/**
	 * Initialize
	 */
	function init() {
		// Register event listeners
		$('main-btn').addEventListener('click', loadMain);
		$('transactions-btn').addEventListener('click', loadTransactions);
		$('trade-btn').addEventListener('click', loadTrade);
		initUser();
	}

	function initUser() {
		
		user_id = Math.random().toString(36).substring(2, 15);
		password = Math.random().toString(36).substring(2, 15);
		var url = './user?user_id=' + user_id + '&password=' + password;
		var req = JSON.stringify({
			user_id : user_id,
			password : password
		});
		ajax('POST', url, req, function(res){loadMain()});
	}

	// -----------------------------------
	// Helper Functions
	// -----------------------------------

	/**
	 * A helper function that makes a navigation button active
	 * 
	 * @param btnId -
	 *            The id of the navigation button
	 */
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');

		// deactivate all navigation buttons
		for (var i = 0; i < btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}

		// active the one that has id = btnId
		var btn = $(btnId);
		btn.className += ' active';
	}

	function showLoadingMessage(msg) {
		console.log(msg);
	}

	function showWarningMessage(msg) {
		console.log(msg);
	}

	function showErrorMessage(msg) {
		console.log(msg);
	}

	/**
	 * A helper function that creates a DOM element <tag options...>
	 * 
	 * @param tag
	 * @param options
	 * @returns
	 */
	function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}

		var element = document.createElement(tag);

		for ( var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}

		return element;
	}

	/**
	 * AJAX helper
	 * 
	 * @param method -
	 *            GET|POST|PUT|DELETE
	 * @param url -
	 *            API end point
	 * @param callback -
	 *            This the successful callback
	 * @param errorHandler -
	 *            This is the failed callback
	 */
	function ajax(method, url, data, callback, errorHandler) {
		var xhr = new XMLHttpRequest();

		xhr.open(method, url, true);

		xhr.onload = function() {
			switch (xhr.status) {
			case 200:
	             if (typeof callback == "function") {
	                 callback(xhr.responseText);
	             }
				break;
			case 403:
				onSessionInvalid();
				break;
			case 401:
				errorHandler();
				break;
			}
		};

		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorHandler();
		};

		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}

	// -------------------------------------
	// AJAX call server-side APIs
	// -------------------------------------

	function loadMain() {
		console.log('loadMain');
		activeBtn('main-btn');

		// The request parameters
		var url = './user';
		var params = 'user_id=' + user_id + '&password=' + password;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading user stat...');

		// make AJAX call
		ajax('GET', url + '?' + params, req,
		// successful callback
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No user found.');
			} else {
				var itemList = $('item-list');
				if (itemList !== null) {
					itemList.parentNode.removeChild(itemList);
				}
				var accountSummary = $('account-summary');
				if (accountSummary !== null) {
					accountSummary.parentNode.removeChild(accountSummary);
				}
				
				var section = $('mainSection');
				
				var ul = $('ul', {
					id : 'account-summary'
				});
				
				var li = $('li', {
					className : 'info'
				});
				
				var div = $('div', {});
				
				var child_1 = $('p', {
					className : 'user-id'
				});
				child_1.innerHTML = 'user-id: ' + items.userId;
				var child_2 = $('p', {
					className : 'user-total'
				});
				child_2.innerHTML = 'Total Asset(USD): ' + items.total;
				var child_3 = $('p', {
					className : 'user-usd'
				});
				child_3.innerHTML = 'USD Asset: ' + items.usd;
				var child_4 = $('p', {
					className : 'user-btc'
				});
				child_4.innerHTML = 'BTC Asset: ' + items.btc;

				div.appendChild(child_1);
				div.appendChild(child_2);
				div.appendChild(child_3);
				div.appendChild(child_4);
				
				ajax('GET', './price', req, function(res){
					var price = JSON.parse(res);
					var child_5 = $('p', {
						className : 'spot-price'
					});
					child_5.innerHTML = 'Spot-Price: ' + price.spot;
					div.appendChild(child_5);
				})
				
				li.appendChild(div);
				ul.appendChild(li);
				section.appendChild(ul);
			}
		},
		// failed callback
		function() {
			showErrorMessage('Cannot load user stat.');
		});
	}

	function loadTransactions() {
		activeBtn('transactions-btn');
		console.log('loadTransaction');
		// The request parameters
		var url = './trade';
		var params = 'user_id=' + user_id + '&password=' + password;
		var req = JSON.stringify({});

		// make AJAX call
		ajax('GET', url + '?' + params, req, function(res) {
			var transactions = JSON.parse(res);
			listTransactions(transactions);

		}, function() {
			showErrorMessage('Cannot load transactions.');
		});
	}

	function loadTrade() {
		activeBtn('trade-btn');

		var itemList = $('item-list');
		if (itemList !== null) {
			itemList.parentNode.removeChild(itemList);
		}
		var accountSummary = $('account-summary');
		if (accountSummary !== null) {
			accountSummary.parentNode.removeChild(accountSummary);
		}
		
		var section = $('mainSection');
		
		var ul = $('ul', {
			id : 'item-list'
		});
		
		var li = $('li', {
			className : 'item'
		});
		
		var div1 = $('div', {});
		var p1 = $('p', {});
		p1.innerHTML = 'Target Price(USD)';
		var text1 = $('textarea', {
			className : 'text',
			maxlength : '20',
			id : 'text-target'
		});
		div1.appendChild(p1);
		div1.appendChild(text1);
		
		var div2 = $('div', {});
		var p2 = $('p', {});
		p2.innerHTML = 'Amount(BTC)';
		var text2 = $('textarea', {
			className : 'text',
			maxlength : '20',
			id : 'text-amount'
		});
		div2.appendChild(p2);
		div2.appendChild(text2);
		
		var div3 = $('div', {});
		var p3 = $('p', {});
		p3.innerHTML = 'Buy/Sell';
		var select = $('select', {
			id : 'buy_sell-select'
		});
		var buy = $('option', {
			value : 'buy'
		});
		buy.innerHTML = 'buy';
		var sell = $('option', {
			value : 'sell'
		});
		sell.innerHTML = 'sell';
		select.appendChild(buy);
		select.appendChild(sell);
		div3.appendChild(p3);
		div3.appendChild(select);
		
		var div4 = $('div', {
			className: 'send-btn'
		});
		div4.innerHTML = ' send';
		div4.onclick = function() {
			send();
		};
		
		li.appendChild(div1);
		li.appendChild(div2);
		li.appendChild(div3);
		li.appendChild(div4);
		
		var li_price = $('li', {
			className : 'info'
		});
		
		var div = $('div', {});
		
		ajax('GET', './price', JSON.stringify({}), function(res){
			var price = JSON.parse(res);
			var child_1 = $('p', {
				className : 'buy-price'
			});
			child_1.innerHTML = 'Buy-Price: ' + price.buy;
			div.appendChild(child_1);
			
			var child_2 = $('p', {
				className : 'spot-price'
			});
			child_2.innerHTML = 'Spot-Price: ' + price.spot;
			div.appendChild(child_2);
			
			var child_3 = $('p', {
				className : 'sell-price'
			});
			child_3.innerHTML = 'Sell-Price: ' + price.sell;
			div.appendChild(child_3);
		})
		
		li_price.appendChild(div);
		
		ul.appendChild(li_price);
		ul.appendChild(li);
		section.appendChild(ul);
	}

	function send() {
		var buy_sell = $('buy_sell-select').value;
		var amount = $('text-amount').value;
		var target = $('text-target').value;
		var itemId = '' + item_counter;
		item_counter = item_counter + 1;
		
		var url = './trade';
		var req = JSON.stringify({
			user_id : user_id,
			item_id : itemId,
			target_price : target,
			amount : amount,
			buy_sell : buy_sell,
			password : password
		});
		var method = 'POST';
		
		ajax(method, url, req,
				
				function(res) {
					loadTrade();
				}, function(res) {
					loadTrade();
				});
	}

	function deleteTransaction(item_id) {

		var li = $('item-' + item_id);
		var cancel = $('cancel-icon-' + item_id);


		// The request parameters
		var url = './trade';
		var req = JSON.stringify({
			user_id : user_id,
			item_id : item_id
		});
		var method = 'DELETE';

		ajax(method, url, req,
		// successful callback
		function(res) {
			loadTransactions();
		});
	}

	// -------------------------------------
	// Create transaction list
	// -------------------------------------

	/**
	 * List transactions
	 * 
	 * @param transactions -
	 *            An array of transaction JSON objects
	 */
	function listTransactions(transactions) {
		// Clear the current results
		var itemList = $('item-list');
		if (itemList !== null) {
			itemList.parentNode.removeChild(itemList);
		}
		var accountSummary = $('account-summary');
		if (accountSummary !== null) {
			accountSummary.parentNode.removeChild(accountSummary);
		}
		
		var section = $('mainSection');
		
		var ul = $('ul', {
			id : 'item-list'
		});
		
		section.appendChild(ul);

		for (var i = 0; i < transactions.length; i++) {
			addTransaction(ul, transactions[i]);
		}
	}

	/**
	 * Add transaction to the list
	 * 
	 * @param itemList -
	 *            The
	 *            <ul id="item-list">
	 *            tag
	 * @param transaction -
	 *            The transaction data (JSON object)
	 */
	function addTransaction(itemList, transaction) {
		var item_id = transaction.item_id;

		// create the <li> tag and specify the id and class attributes
		var li = $('li', {
			id : 'item-' + item_id,
			className : 'item'
		});


		// section
		var section = $('div', {});

		var child_1 = $('p', {
			className : 'item-id'
		});
		child_1.innerHTML = 'Transaction ID: ' + transaction.item_id;
		var child_2 = $('p', {
			className : 'item-amount'
		});
		child_2.innerHTML = 'Transaction Amount(BTC): ' + transaction.amount;
		var child_3 = $('p', {
			className : 'item-target'
		});
		child_3.innerHTML = 'Target Price: ' + transaction.target_price;
		var child_4 = $('p', {
			className : 'item-bs'
		});
		child_4.innerHTML = 'Buy/Sell: ' + transaction.buy_sell;
		section.appendChild(child_1);
		section.appendChild(child_2);
		section.appendChild(child_3);
		section.appendChild(child_4);

		li.appendChild(section);


		// cancel link
		var div = $('div', {
			className : 'cancel-link',
			id : 'cancel_icon-' + item_id
		});
		div.innerHTML = 'Cancel';
		div.onclick = function() {
			deleteTransaction(item_id);
		};

		li.appendChild(div);

		itemList.appendChild(li);
	}

	init();

})();

// END