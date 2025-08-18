import React from 'react';
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
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Paper,
} from '@mui/material';
import { RecommendationResponse } from '../api/client';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import WarningIcon from '@mui/icons-material/Warning';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const Recommendations: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const recommendations = location.state?.recommendations as RecommendationResponse;

  if (!recommendations) {
    navigate('/');
    return null;
  }

  const getRecommendationIcon = (index: number) => {
    switch (index) {
      case 0:
        return <TrendingUpIcon color="success" />;
      case 1:
        return <CheckCircleIcon color="primary" />;
      case 2:
        return <WarningIcon color="warning" />;
      default:
        return <CheckCircleIcon />;
    }
  };

  const getRecommendationColor = (index: number) => {
    switch (index) {
      case 0:
        return 'success';
      case 1:
        return 'primary';
      case 2:
        return 'warning';
      default:
        return 'default';
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/simulation')}
          sx={{ mr: 2 }}
        >
          Back to Simulation
        </Button>
        <Typography variant="h4">
          Smart Recommendations
        </Typography>
      </Box>

      {/* Rationale */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom color="primary">
            Why These Recommendations?
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {recommendations.rationale}
          </Typography>
        </CardContent>
      </Card>

      {/* Top Recommendations */}
      <Grid container spacing={3}>
        {recommendations.top3.map((recommendation, index) => (
          <Grid size={{ xs: 12, md:8 }} key = {index}>
            <Card 
              sx={{ 
                height: '100%',
                border: index === 0 ? '2px solid #4caf50' : '1px solid #e0e0e0',
                position: 'relative'
              }}
            >
              {index === 0 && (
                <Box
                  sx={{
                    position: 'absolute',
                    top: -10,
                    left: '50%',
                    transform: 'translateX(-50%)',
                    backgroundColor: '#4caf50',
                    color: 'white',
                    px: 2,
                    py: 0.5,
                    borderRadius: 1,
                    fontSize: '0.875rem',
                    fontWeight: 'bold',
                  }}
                >
                  BEST CHOICE
                </Box>
              )}
              
              <CardContent sx={{ pt: index === 0 ? 4 : 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  {getRecommendationIcon(index)}
                  <Typography 
                    variant="h6" 
                    sx={{ ml: 1 }}
                    color={getRecommendationColor(index)}
                  >
                    #{index + 1} {recommendation.label}
                  </Typography>
                </Box>

                <Typography variant="h4" color="primary" gutterBottom>
                  {recommendation.totalCost.toFixed(2)} €
                </Typography>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {recommendation.explanation}
                </Typography>

                {recommendation.details && (
                  <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      Details:
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {JSON.stringify(recommendation.details, null, 2)}
                    </Typography>
                  </Paper>
                )}

                <Button
                  variant={index === 0 ? "contained" : "outlined"}
                  fullWidth
                  onClick={() => navigate('/checkout', { 
                    state: { 
                      selectedRecommendation: recommendation,
                      recommendationIndex: index
                    } 
                  })}
                  sx={{ mt: 'auto' }}
                >
                  {index === 0 ? 'Choose This Option' : 'Select This Option'}
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Additional Information */}
      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            How We Calculate Recommendations
          </Typography>
          <List>
            <ListItem>
              <ListItemIcon>
                <CheckCircleIcon color="success" />
              </ListItemIcon>
              <ListItemText 
                primary="Cost Optimization"
                secondary="We compare all available roaming packs and pay-as-you-go rates to find the most cost-effective solution"
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CheckCircleIcon color="success" />
              </ListItemIcon>
              <ListItemText 
                primary="Coverage Analysis"
                secondary="We analyze your trip countries and match them with pack coverage (region vs country-specific)"
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CheckCircleIcon color="success" />
              </ListItemIcon>
              <ListItemText 
                primary="Usage Patterns"
                secondary="We consider your daily usage patterns and calculate potential overage costs"
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CheckCircleIcon color="success" />
              </ListItemIcon>
              <ListItemText 
                primary="Validity Matching"
                secondary="We ensure pack validity periods align with your trip duration to avoid unnecessary costs"
              />
            </ListItem>
          </List>
        </CardContent>
      </Card>

      {/* Action Buttons */}
      <Box sx={{ mt: 3, textAlign: 'center' }}>
        <Button
          variant="outlined"
          onClick={() => navigate('/')}
          sx={{ mr: 2 }}
        >
          Plan New Trip
        </Button>
        <Button
          variant="contained"
          onClick={() => navigate('/simulation')}
        >
          View Full Simulation
        </Button>
      </Box>
    </Box>
  );
};

export default Recommendations;
