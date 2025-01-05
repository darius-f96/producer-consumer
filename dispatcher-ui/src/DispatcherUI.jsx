import React, { useState } from 'react';

function DispatcherUI() {
  const [messagesConsumer1, setMessagesConsumer1] = useState('');
  const [messagesConsumer2, setMessagesConsumer2] = useState('');

  const [tomcatProducerMsg, setTomcatProducerMsg] = useState('');
  const [jettyProducerMsg, setJettyProducerMsg] = useState('');

  const DISPATCHER_URL = "http://localhost:8085/dispatcher-app";
  const TOMCAT_CONSUMER = "/dispatcher/tomcat-consumer";
  const TOMCAT_PRODUCER = "/dispatcher/tomcat-producer";
  const JETTY_CONSUMER = "/dispatcher/jetty-consumer";
  const JETTY_PRODUCER = "/dispatcher/jetty-producer";

  const doPost = async (endpoint, params) => {
    try {
      const resp = await fetch(DISPATCHER_URL + endpoint + (params ? `?${params}` : ''), {
        method: 'POST'
      });
      if (!resp.ok) {
        const text = await resp.text();
        throw new Error(`POST ${endpoint} failed: ${resp.status} - ${text}`);
      }
      const data = await resp.text();
      alert(data);
    } catch (err) {
      console.error(err);
      alert(err.message);
    }
  };

  const startConsumer1 = () => doPost(`${TOMCAT_CONSUMER}/start`);
  const stopConsumer1  = () => doPost(`${TOMCAT_CONSUMER}/stop`);

  const getConsumer1Messages = () => {
    fetch(`${DISPATCHER_URL}${TOMCAT_CONSUMER}/messages`, {
      method: "GET"
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.text();
      })
      .then((data) => {
        setMessagesConsumer1(data);
      })
      .catch((error) => {
        console.error("Error fetching tomcat consumer messages:", error);
        alert("Failed to fetch tomcat consumer messages.");
      });
  };
  

  const startConsumer2 = () => doPost(`${JETTY_CONSUMER}/start`);
  const stopConsumer2  = () => doPost(`${JETTY_CONSUMER}/stop`);
  const getConsumer2Messages = () => {
    fetch(`${DISPATCHER_URL}${JETTY_CONSUMER}/messages`, {
      method: "GET"
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.text();
      })
      .then((data) => {
        setMessagesConsumer2(data);
      })
      .catch((error) => {
        console.error("Error fetching jetty consumer messages:", error);
        alert("Failed to fetch jetty consumer messages.");
      });
  };

  const sendTotomcatProducer = () => {
    const params = `msg=${encodeURIComponent(tomcatProducerMsg)}`;
    doPost(`${TOMCAT_PRODUCER}/send`, params);
  };

  const sendToJettyProducer = () => {
    const params = `msg=${encodeURIComponent(jettyProducerMsg)}`;
    doPost(`${JETTY_PRODUCER}/send`, params);
  };

  return (
    <div style={{ margin: '20px' }}>
      <h1>Dispatcher UI</h1>

      <section>
        <h2>Tomcat Consumer</h2>
        <button onClick={startConsumer1}>Start Consumer</button>
        <button onClick={stopConsumer1}>Stop Consumer</button>
        <button onClick={getConsumer1Messages}>Get Consumer Messages</button>
        <pre>{messagesConsumer1}</pre>
      </section>

      <section>
        <h2>Jetty Consumer</h2>
        <button onClick={startConsumer2}>Start Consumer</button>
        <button onClick={stopConsumer2}>Stop Consumer</button>
        <button onClick={getConsumer2Messages}>Get Consumer Messages</button>
        <pre>{messagesConsumer2}</pre>
      </section>

      <section>
        <h2>Tomcat Producer</h2>
        <input
          type="text"
          placeholder="Message to producer"
          value={tomcatProducerMsg}
          onChange={(e) => setTomcatProducerMsg(e.target.value)}
        />
        <button onClick={sendTotomcatProducer}>Send to Producer</button>
      </section>

      <section>
        <h2>Jetty Producer</h2>
        <input
          type="text"
          placeholder="Message to producer"
          value={jettyProducerMsg}
          onChange={(e) => setJettyProducerMsg(e.target.value)}
        />
        <button onClick={sendToJettyProducer}>Send to Producer</button>
      </section>
    </div>
  );
}

export default DispatcherUI;
