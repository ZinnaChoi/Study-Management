import React, {useEffect, useState} from 'react';
import axios from 'axios';

function App() {
  const [hello, setHello] = useState('')

  useEffect(() => {
      axios.get('/api/hello')
      .then(response => setHello(response.data))
      .catch(error => console.log(error))
  }, []);

  return (
    <div>
        백엔드와 연동 : {hello}
    </div>
);

}

export default App;
