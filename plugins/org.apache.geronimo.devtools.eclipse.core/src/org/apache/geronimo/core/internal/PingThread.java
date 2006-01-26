/**
 * Copyright 2004, 2005 The Apache Software Foundation or its licensors, as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.core.internal;

import org.eclipse.wst.server.core.util.SocketUtil;

public class PingThread extends Thread {

	private static final int PING_DELAY = 5000;
	private static final int PING_INTERVAL = 5000;
	private static final int MAX_PINGS = 40;

	private GeronimoServerBehaviour geronimoServer;

	public PingThread(GeronimoServerBehaviour geronimoServer) {
		super();
		this.geronimoServer = geronimoServer;
		this.setDaemon(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		if (!SocketUtil.isLocalhost(geronimoServer.getServer().getHost())) {
			return;
		}

		try {
			sleep(PING_DELAY);
		} catch (InterruptedException e) {
			// ignore
		}

		for (int tries = MAX_PINGS; tries > 0; tries--) {

			if (geronimoServer.isKernelFullyStarted()) {
				Trace.trace(Trace.INFO, "Ping: success");
				geronimoServer.setServerStarted();
				return;
			}

			Trace.trace(Trace.INFO, "Ping: failed");

			try {
				sleep(PING_INTERVAL);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		Trace.trace(Trace.SEVERE, "Ping: Can't ping for server startup.");
		geronimoServer.getServer().stop(false);

	}

}