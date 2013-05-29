import BaseHTTPServer, json

NUM_PEERS_FOR_GAME=3

peers = set()
told  = set()

class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
	def do_GET(self):
		global peers, told
		
		player_name, peer_port = self.path.split('/')[-2:]
		
		if len(peers) < NUM_PEERS_FOR_GAME:
			if player_name in { n for a,p,n in peers } and (self.client_address[0], peer_port, player_name) not in peers:
				print >> self.wfile, "nickname-present"
				#print "The nickname {} has already been taken. Choose a different nickname.".format(player_name)
				return
			
			# Add to peer set
			peers |= { (self.client_address[0], peer_port, player_name) }
			print peers
			
			self.send_response(200)
			self.end_headers()
			print >> self.wfile, "wait"
		
		if len(peers) == NUM_PEERS_FOR_GAME:
			self.send_response(200)
			self.end_headers()
			print >> self.wfile, "start"
			print >> self.wfile, json.dumps(list(enumerate(peers)))
			told |= { (self.client_address[0], peer_port, player_name) }
			if told == peers:
				peers = set()
				told = set()
				print '# TOLD EVERYONE. RESETTING FOR A NEW GAME...'

def run(server_class=BaseHTTPServer.HTTPServer,
        handler_class=BaseHTTPServer.BaseHTTPRequestHandler):
    server_address = ('', 8080)
    httpd = server_class(server_address, handler_class)
    print "starting server..."
    httpd.serve_forever()


run(handler_class=Handler)
