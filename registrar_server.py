import BaseHTTPServer, json

peers = set()

class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
	def do_GET(self):
		global peers
		
		if len(peers) < 4:
			player_name, peer_port = self.path.split('/')[-2:]
			peers |= { (self.client_address[0], peer_port, player_name) }
			print peers
			
			self.send_response(200)
			self.end_headers()
			self.wfile.write("wait\n")
		
		if len(peers) == 4:
			self.send_response(200)
			self.end_headers()
			print >> self.wfile, "start"
			print >> self.wfile, json.dumps(list(enumerate(peers)))

def run(server_class=BaseHTTPServer.HTTPServer,
        handler_class=BaseHTTPServer.BaseHTTPRequestHandler):
    server_address = ('', 8080)
    httpd = server_class(server_address, handler_class)
    httpd.serve_forever()


run(handler_class=Handler)
