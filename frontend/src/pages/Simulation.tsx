import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Chip,
  Alert,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Divider,
} from '@mui/material';
import { api, endpoints, SimulationRequest, SimulationResponse } from '../api/client';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const Simulation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [simulationData, setSimulationData] = useState<SimulationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const simulationRequest = location.state?.simulationRequest as SimulationRequest;

  useEffect(() => {
    if (!simulationRequest) {
      navigate('/');
      return;
    }
    runSimulation();
  }, [simulationRequest, navigate]);

  const runSimulation = async () => {
    try {
      setLoading(true);
      setError(null);

      // Convert the request to match backend format
      const backendRequest = {
        userId: simulationRequest.userId,
        trips: simulationRequest.trips.map(trip => ({
          countryCode: trip.countryCode,
          startDate: trip.startDate,
          endDate: trip.endDate,
        })),
        profile: {
          avgDailyMb: simulationRequest.profile.avgDailyMb,
          avgDailyMin: simulationRequest.profile.avgDailyMin,
          avgDailySms: simulationRequest.profile.avgDailySms,
        },
      };

      const response = await api.post(endpoints.simulate, backendRequest);
      setSimulationData(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to run simulation');
      console.error('Simulation error:', err);
    } finally {
      setLoading(false);
    }
  };

  const getRecommendations = async () => {
    try {
      setLoading(true);
      setError(null);

      const backendRequest = {
        userId: simulationRequest.userId,
        trips: simulationRequest.trips.map(trip => ({
          countryCode: trip.countryCode,
          startDate: trip.startDate,
          endDate: trip.endDate,
        })),
        profile: {
          avgDailyMb: simulationRequest.profile.avgDailyMb,
          avgDailyMin: simulationRequest.profile.avgDailyMin,
          avgDailySms: simulationRequest.profile.avgDailySms,
        },
      };

      const response = await api.post(endpoints.recommendation, backendRequest);
      navigate('/recommendations', { state: { recommendations: response.data } });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to get recommendations');
      console.error('Recommendations error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (!simulationRequest) {
    return null;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Simulation Results
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Trip Summary */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Trip Summary
          </Typography>
                      <Grid container spacing={2}>
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography variant="h6" color="primary">
                  {simulationData?.summary.days || 0} Days
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Trip Duration
                </Typography>
              </Grid>
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography variant="h6" color="primary">
                  {simulationData?.summary.totalNeed.gb.toFixed(2) || 0} GB
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Data Need
                </Typography>
              </Grid>
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography variant="h6" color="primary">
                  {simulationData?.summary.totalNeed.min || 0} min
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Voice Need
                </Typography>
              </Grid>
            </Grid>
        </CardContent>
      </Card>

      {/* Simulation Results */}
      {simulationData && (
        <>
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h5" gutterBottom>
                Cost Comparison
              </Typography>
              
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Option</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Packs</TableCell>
                      <TableCell>Total Cost</TableCell>
                      <TableCell>Coverage</TableCell>
                      <TableCell>Validity</TableCell>
                      <TableCell>Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {simulationData.options.map((option, index) => (
                      <TableRow key={index}>
                        <TableCell>
                          {option.kind === 'pack' ? `Pack ${option.packId}` : 'Pay-as-you-go'}
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={option.kind === 'pack' ? 'Pack' : 'PAYG'}
                            color={option.kind === 'pack' ? 'primary' : 'secondary'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>{option.nPacks}</TableCell>
                        <TableCell>
                          <Typography variant="h6" color="primary">
                            {option.totalCost.toFixed(2)} {option.currency}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          {option.coverageHit ? (
                            <Chip
                              icon={<CheckCircleIcon />}
                              label="Covered"
                              color="success"
                              size="small"
                            />
                          ) : (
                            <Chip
                              icon={<WarningIcon />}
                              label="Partial"
                              color="warning"
                              size="small"
                            />
                          )}
                        </TableCell>
                        <TableCell>
                          {option.validityOk ? (
                            <Chip
                              icon={<CheckCircleIcon />}
                              label="Valid"
                              color="success"
                              size="small"
                            />
                          ) : (
                            <Chip
                              icon={<WarningIcon />}
                              label="Short"
                              color="error"
                              size="small"
                            />
                          )}
                        </TableCell>
                        <TableCell>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => navigate('/checkout', { 
                              state: { 
                                selectedOption: option,
                                simulationRequest,
                                simulationData 
                              } 
                            })}
                          >
                            Select
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>

          {/* Warnings */}
          {simulationData.warnings && simulationData.warnings.length > 0 && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="h5" gutterBottom color="warning.main">
                  Warnings
                </Typography>
                {simulationData.warnings.map((warning, index) => (
                  <Alert key={index} severity="warning" sx={{ mb: 1 }}>
                    {warning}
                  </Alert>
                ))}
              </CardContent>
            </Card>
          )}
        </>
      )}

      {/* Action Buttons */}
      <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center' }}>
        <Button
          variant="outlined"
          onClick={() => navigate('/')}
        >
          Back to Planner
        </Button>
        
        <Button
          variant="contained"
          onClick={getRecommendations}
          startIcon={<TrendingUpIcon />}
          disabled={loading}
        >
          Get Smart Recommendations
        </Button>
      </Box>
    </Box>
  );
};

export default Simulation;
