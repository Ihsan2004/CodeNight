import React, { useState } from 'react';
import { api, endpoints } from '../api/client';
import { Box, Button, Typography, Alert, Paper } from '@mui/material';

const BackendTest: React.FC = () => {
  const [testResults, setTestResults] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const addResult = (message: string) => {
    setTestResults(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`]);
  };

  const testBackendConnection = async () => {
    setLoading(true);
    setTestResults([]);
    
    try {
      // Test 1: Basic connectivity to simulation endpoint
      addResult('Testing simulation endpoint connectivity...');
      const simTestResponse = await api.get('/simulate/test');
      addResult(`✅ Simulation test: ${simTestResponse.data.message}`);
      
      // Test 2: Catalog endpoint
      addResult('Testing catalog endpoint...');
      const catalogResponse = await api.get(endpoints.catalog);
      addResult(`✅ Catalog: ${catalogResponse.data.countries?.length || 0} countries, ${catalogResponse.data.packs?.length || 0} packs`);
      
      // Test 3: Test catalog test endpoint
      addResult('Testing catalog test endpoint...');
      const catalogTestResponse = await api.get('/catalog/test');
      addResult(`✅ Catalog test: ${catalogTestResponse.data.message}`);
      
      addResult('🎉 All tests passed! Backend is accessible.');
      
    } catch (error: any) {
      addResult(`❌ Error: ${error.message}`);
      if (error.response) {
        addResult(`Status: ${error.response.status}`);
        addResult(`Data: ${JSON.stringify(error.response.data)}`);
      } else if (error.request) {
        addResult('No response received - check if backend is running');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Backend Connection Test
      </Typography>
      
      <Button 
        variant="contained" 
        onClick={testBackendConnection}
        disabled={loading}
        sx={{ mb: 2 }}
      >
        {loading ? 'Testing...' : 'Test Backend Connection'}
      </Button>

      <Paper sx={{ p: 2, maxHeight: 400, overflow: 'auto' }}>
        <Typography variant="h6" gutterBottom>
          Test Results:
        </Typography>
        {testResults.length === 0 ? (
          <Typography color="text.secondary">
            Click the test button to check backend connectivity
          </Typography>
        ) : (
          testResults.map((result, index) => (
            <Typography key={index} variant="body2" sx={{ mb: 1, fontFamily: 'monospace' }}>
              {result}
            </Typography>
          ))
        )}
      </Paper>

      <Alert severity="info" sx={{ mt: 2 }}>
        <Typography variant="body2">
          <strong>Expected Backend URL:</strong> http://localhost:8000/api<br/>
          <strong>Frontend URL:</strong> http://localhost:3000<br/>
          <strong>Make sure:</strong> Your Spring Boot backend is running on port 8000
        </Typography>
      </Alert>
    </Box>
  );
};

export default BackendTest;
